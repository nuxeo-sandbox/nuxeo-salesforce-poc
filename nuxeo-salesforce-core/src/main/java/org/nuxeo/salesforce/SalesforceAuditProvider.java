/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.salesforce;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.automation.core.util.Paginable;
import org.nuxeo.ecm.automation.core.util.Properties;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.platform.audit.api.AuditPageProvider;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.api.LogEntryList;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.core.GenericPageProviderDescriptor;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;

/**
 * This operation is restricted to administrators and members groups (can be configured). The document in input is the
 * 'root' of all documents returned in audit listing.
 *
 * @since 7.10
 */
@Operation(id = SalesforceAuditProvider.ID, category = "Salesforce", label = "This operation is restricted to administrators and members groups (can be configured). The document in input is the 'root' of all documents returned in audit listing.", description = "", addToStudio = false)
public class SalesforceAuditProvider {

    public static final String ID = "Salesforce.AuditProvider";

    public static final String CURRENT_USERID_PATTERN = "$currentUser";

    public static final String CURRENT_REPO_PATTERN = "$currentRepository";

    private static final String SORT_PARAMETER_SEPARATOR = " ";

    public static final String DESC = "DESC";

    public static final String ASC = "ASC";

    @Context
    protected OperationContext context;

    @Context
    protected CoreSession session;

    @Context
    protected PageProviderService ppService;

    @Param(name = "providerName", required = false)
    protected String providerName;

    @Param(name = "query", required = false)
    protected String query;

    @Param(name = "language", required = false, widget = Constants.W_OPTION, values = { NXQL.NXQL })
    protected String lang = NXQL.NXQL;

    @Param(name = "page", required = false)
    @Deprecated
    protected Integer page;

    @Param(name = "currentPageIndex", required = false)
    protected Integer currentPageIndex;

    @Param(name = "pageSize", required = false)
    protected Integer pageSize;

    /**
     * @deprecated since 6.0 use instead {@link #sortBy and @link #sortOrder}.
     */
    @Deprecated
    @Param(name = "sortInfo", required = false)
    protected StringList sortInfoAsStringList;

    @Param(name = "queryParams", required = false)
    protected StringList strParameters;

    @Param(name = "namedQueryParams", required = false)
    protected Properties namedQueryParams;

    /**
     * @deprecated since 6.0, not used in operation.
     */
    @Deprecated
    @Param(name = "maxResults", required = false)
    protected Integer maxResults = 100;

    /**
     * @since 6.0
     */
    @Param(name = "sortBy", required = false, description = "Sort by " + "properties (separated by comma)")
    protected String sortBy;

    /**
     * @since 6.0
     */
    @Param(name = "sortOrder", required = false, description = "Sort order, " + "ASC or DESC", widget = Constants.W_OPTION, values = {
            ASC, DESC })
    protected String sortOrder;

    @SuppressWarnings("unchecked")
    @OperationMethod
    public Paginable<LogEntry> run(DocumentModel input) throws IOException {

        List<SortInfo> sortInfos = null;
        if (sortInfoAsStringList != null) {
            sortInfos = new ArrayList<SortInfo>();
            for (String sortInfoDesc : sortInfoAsStringList) {
                SortInfo sortInfo;
                if (sortInfoDesc.contains(SORT_PARAMETER_SEPARATOR)) {
                    String[] parts = sortInfoDesc.split(SORT_PARAMETER_SEPARATOR);
                    sortInfo = new SortInfo(parts[0], Boolean.parseBoolean(parts[1]));
                } else {
                    sortInfo = new SortInfo(sortInfoDesc, true);
                }
                sortInfos.add(sortInfo);
            }
        } else {
            // Sort Info Management
            if (!StringUtils.isBlank(sortBy)) {
                sortInfos = new ArrayList<>();
                String[] sorts = sortBy.split(",");
                String[] orders = null;
                if (!StringUtils.isBlank(sortOrder)) {
                    orders = sortOrder.split(",");
                }
                for (int i = 0; i < sorts.length; i++) {
                    String sort = sorts[i];
                    boolean sortAscending = (orders != null && orders.length > i && "asc".equals(orders[i].toLowerCase()));
                    sortInfos.add(new SortInfo(sort, sortAscending));
                }
            }
        }

        Object[] parameters = null;

        if (strParameters != null && !strParameters.isEmpty()) {
            parameters = strParameters.toArray(new String[strParameters.size()]);
            // expand specific parameters
            for (int idx = 0; idx < parameters.length; idx++) {
                String value = (String) parameters[idx];
                if (value.equals(CURRENT_USERID_PATTERN)) {
                    parameters[idx] = session.getPrincipal().getName();
                } else if (value.equals(CURRENT_REPO_PATTERN)) {
                    parameters[idx] = session.getRepositoryName();
                }
            }
        }
        if (parameters == null) {
            parameters = new Object[0];
        }

        Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY, (Serializable) session);

        if (query == null && (providerName == null || providerName.length() == 0)) {
            // provide a defaut provider
            providerName = "AUDIT_BROWSER";
        }

        Long targetPage = null;
        if (page != null) {
            targetPage = page.longValue();
        }
        if (currentPageIndex != null) {
            targetPage = currentPageIndex.longValue();
        }
        Long targetPageSize = null;
        if (pageSize != null) {
            targetPageSize = pageSize.longValue();
        }

        if (query != null) {

            AuditPageProvider app = new AuditPageProvider();
            app.setProperties(props);
            GenericPageProviderDescriptor desc = new GenericPageProviderDescriptor();
            desc.setPattern(query);
            app.setParameters(parameters);
            app.setDefinition(desc);
            app.setSortInfos(sortInfos);
            app.setPageSize(targetPageSize);
            app.setCurrentPage(targetPage);
            return new LogEntryList(app);
        } else {

            DocumentModel searchDoc = null;
            if (namedQueryParams != null && namedQueryParams.size() > 0) {
                String docType = ppService.getPageProviderDefinition(providerName).getWhereClause().getDocType();
                searchDoc = session.createDocumentModel(docType);
                DocumentHelper.setProperties(session, searchDoc, namedQueryParams);
            }

            PageProvider<LogEntry> pp = (PageProvider<LogEntry>) ppService.getPageProvider(providerName, searchDoc,
                    sortInfos, targetPageSize, targetPage, props, parameters);
            LogEntryList entries = new LogEntryList(pp);
            LogEntryList result = (LogEntryList) entries.clone();
            result.clear();
            for (LogEntry entry : entries) {
                if (entry.getDocPath() != null && entry.getDocPath().contains(input.getPathAsString())) {
                    result.add(entry);
                }
            }
            return result;
        }

    }
}
