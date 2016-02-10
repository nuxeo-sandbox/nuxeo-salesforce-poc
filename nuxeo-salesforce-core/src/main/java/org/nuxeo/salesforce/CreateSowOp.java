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
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Operation(
        id = CreateSowOp.ID,
        category = Constants.CAT_CONVERSION,
        label = "Create SOW",
        description = "Create Statement of Work From template")
public class CreateSowOp {

    public static final String ID = "Conversion.CreateSowOp";

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(CreateSowOp.class);

    @Context
    protected CoreSession session;

    @Param(name = "account", required = true)
    protected Properties account;

    @Param(name = "opportunity", required = true)
    protected Properties opportunity;

    @Param(name = "owner", required = true)
    protected Properties owner;

    @Param(name = "sections", required = true)
    protected List<String> sections;

    @OperationMethod
    public DocumentModel run(DocumentModel template) {

        // get opportunity folder
        DocumentModelList list =
                session.query(
                        "Select * From Document WHERE sf:objectId = '"+
                                opportunity.get("Id")+"'");

        if (list.size()==0) return null;

        DocumentModel opportunityDoc = list.get(0);

        // create Doc

        DocumentModel sow = session.createDocumentModel(opportunityDoc.getPathAsString(),"SOW","File");
        sow.addFacet("TemplateBased");
        sow.addFacet("salesforceTemplate");

        //Map properties
        //opportunity
        sow.setPropertyValue("dc:title","SOW - "+opportunity.get("Name"));

        //Account
        sow.setPropertyValue("sfa:name",account.get("Name"));
        sow.setPropertyValue("sfa:street",account.get("BillingStreet"));
        sow.setPropertyValue("sfa:city",account.get("BillingCity"));
        sow.setPropertyValue("sfa:country",account.get("BillingCountry"));
        sow.setPropertyValue("sfa:postalcode",account.get("BillingPostalCode"));


        //Owner
        sow.setPropertyValue("sfu:name",account.get("Name"));
        sow.setPropertyValue("sfu:title",account.get("Title"));
        sow.setPropertyValue("sfu:phone",account.get("Phone"));
        sow.setPropertyValue("sfu:email",account.get("Email"));

        //sections
        sow.setPropertyValue("sections:sections", (Serializable) sections);

        sow = session.createDocument(sow);
        TemplateBasedDocument templateBasedDocument = sow.getAdapter(TemplateBasedDocument.class);
        templateBasedDocument.setTemplate(template,false);

        Blob rendered = templateBasedDocument.renderWithTemplate(template.getName());

        sow.setPropertyValue("file:content", (Serializable) rendered);
        session.saveDocument(sow);
        return sow;
    }
}