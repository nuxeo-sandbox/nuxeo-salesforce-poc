/*
 * (C) Copyright ${year} Nuxeo SA (http://nuxeo.com/) and contributors.
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
 */

package org.nuxeo.salesforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.Properties;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;

import java.io.Serializable;
import java.util.List;


@Operation(
        id = RenderTemplateOp.ID,
        category = Constants.CAT_CONVERSION,
        label = "Render Template",
        description = "Create Opportunity Doc From template")
public class RenderTemplateOp {

    public static final String ID = "Conversion.RenderTemplateOp";

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(RenderTemplateOp.class);

    @Context
    protected CoreSession session;

    @Param(name = "templateUid", required = true)
    protected String templateUid;

    @Param(name = "account", required = true)
    protected Properties account;

    @Param(name = "opportunity", required = true)
    protected Properties opportunity;

    @Param(name = "owner", required = true)
    protected Properties owner;

    @Param(name = "contacts", required = true)
    protected Properties contacts;

    @Param(name = "sections", required = true)
    protected List<String> sections;

    @OperationMethod
    public DocumentModel run() {

        // get opportunity folder
        DocumentModelList list =
                session.query(
                        "Select * From Document WHERE sfop:objectId = '" +
                                opportunity.get("Id") + "'");

        if (list.size() == 0) return null;

        DocumentModel opportunityDoc = list.get(0);

        // get template
        DocumentModel template = session.getDocument(new IdRef(templateUid));
        String templateName = (String) template.getPropertyValue("dc:title");

        // create Doc

        DocumentModel renderedTemplate = session.createDocumentModel(opportunityDoc.getPathAsString(), templateName, "File");
        renderedTemplate.addFacet("TemplateBased");
        renderedTemplate.addFacet("salesforceTemplate");

        //Map properties
        //opportunity
        renderedTemplate.setPropertyValue("dc:title", templateName + " for " + opportunity.get("Name"));
        renderedTemplate.setPropertyValue("sfop:name", opportunity.get("Name"));

        //Account
        renderedTemplate.setPropertyValue("sfa:name", account.get("Name"));
        renderedTemplate.setPropertyValue("sfa:street", account.get("BillingStreet"));
        renderedTemplate.setPropertyValue("sfa:city", account.get("BillingCity"));
        renderedTemplate.setPropertyValue("sfa:country", account.get("BillingCountry"));
        renderedTemplate.setPropertyValue("sfa:postalcode", account.get("BillingPostalCode"));


        //Owner
        renderedTemplate.setPropertyValue("sfu:name", account.get("Name"));
        renderedTemplate.setPropertyValue("sfu:title", account.get("Title"));
        renderedTemplate.setPropertyValue("sfu:phone", account.get("Phone"));
        renderedTemplate.setPropertyValue("sfu:email", account.get("Email"));

        //sections
        renderedTemplate.setPropertyValue("sections:sections", (Serializable) sections);

        renderedTemplate = session.createDocument(renderedTemplate);
        TemplateBasedDocument templateBasedDocument = renderedTemplate.getAdapter(TemplateBasedDocument.class);
        templateBasedDocument.setTemplate(template, false);

        Blob rendered = templateBasedDocument.renderWithTemplate(template.getName());

        renderedTemplate.setPropertyValue("file:content", (Serializable) rendered);
        session.saveDocument(renderedTemplate);
        return renderedTemplate;
    }
}
