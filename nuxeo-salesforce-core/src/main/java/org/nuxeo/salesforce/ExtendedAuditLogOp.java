/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.salesforce;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.audit.api.AuditLogger;
import org.nuxeo.ecm.platform.audit.api.ExtendedInfo;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.impl.ExtendedInfoImpl;

import java.security.Principal;
import java.util.*;


@Operation(id = ExtendedAuditLogOp.ID, category = Constants.CAT_SERVICES, label = "Log Event In Audit", description = "Log events into audit")
public class ExtendedAuditLogOp {

    public static final String ID = "ExtendedAuditLogOp";

    @Context
    protected AuditLogger logger;

    @Context
    protected OperationContext ctx;

    @Param(name = "event")
    protected String event;

    @Param(name = "category", required = false)
    protected String category = "Automation";

    @Param(name = "comment", required = false)
    protected String comment = "";

    @Param(name = "pageNumber", required = true)
    protected int pageNumber;

    @Param(name = "timeSpentSeconds", required = true)
    protected int timeSpentSeconds;

    @OperationMethod
    public DocumentModel run(DocumentModel doc) {
        Principal principal = ctx.getPrincipal();
        String uname = getPrincipalName(principal);
        LogEntry entry = newEntry(doc, uname, new Date());
        logger.addLogEntries(Collections.singletonList(entry));
        return doc;
    }

    @OperationMethod
    public DocumentModelList run(DocumentModelList docs) {
        List<LogEntry> entries = new ArrayList<LogEntry>();
        Date date = new Date();
        Principal principal = ctx.getPrincipal();
        String uname = getPrincipalName(principal);
        for (DocumentModel doc : docs) {
            entries.add(newEntry(doc, uname, date));
        }
        logger.addLogEntries(entries);
        return docs;
    }

    protected LogEntry newEntry(DocumentModel doc, String principal, Date date) {
        LogEntry entry = logger.newLogEntry();
        entry.setEventId(event);
        entry.setEventDate(new Date());
        entry.setCategory(category);
        entry.setDocUUID(doc.getId());
        entry.setDocPath(doc.getPathAsString());
        entry.setComment(comment);
        entry.setPrincipalName(principal);
        entry.setDocType(doc.getType());
        entry.setRepositoryId(doc.getRepositoryName());
        entry.setDocLifeCycle(doc.getCurrentLifeCycleState());
        Map<String,ExtendedInfo> extended = new HashMap<>();
        extended.put("pageNumber",ExtendedInfoImpl.createExtendedInfo((long) pageNumber));
        extended.put("timeSpentSeconds",ExtendedInfoImpl.createExtendedInfo((long) timeSpentSeconds));
        entry.setExtendedInfos(extended);
        return entry;
    }

    private String getPrincipalName(Principal principal) {
        String uname;
        if (principal instanceof NuxeoPrincipal) {
            uname = ((NuxeoPrincipal) principal).getActingUser();
        } else {
            uname = principal.getName();
        }
        return uname;
    }

}
