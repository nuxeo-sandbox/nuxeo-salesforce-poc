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

    @OperationMethod
    public DocumentModel run(DocumentModel template) {
        DocumentModel source = session.createDocumentModel("File");
        source.addFacet("TemplateBased");
        TemplateBasedDocument templateBasedDocument = source.getAdapter(TemplateBasedDocument.class);
        templateBasedDocument.setTemplate(template,false);

        //Map properties
        source.addFacet("salesforceTemplate");

        //opportunity
        source.setPropertyValue("dc:title",opportunity.get("Name"));

        //Account
        source.setPropertyValue("sfa:name",account.get("Name"));

        //Owner
        source.setPropertyValue("sfu:name",account.get("Name"));
        source.setPropertyValue("sfu:title",account.get("Title"));
        source.setPropertyValue("sfu:phone",account.get("Phone"));
        source.setPropertyValue("sfu:email",account.get("Email"));

        Blob rendered = templateBasedDocument.renderWithTemplate(template.getName());

        // get opportunity folder
        DocumentModelList list =
                session.query(
                        "Select * From Document WHERE sf:objectId = '"+
                                opportunity.get("objectId")+"'");

        if (list.size()==0) return null;

        DocumentModel opportunity = list.get(0);

        DocumentModel sow = session.createDocumentModel(opportunity.getPathAsString(),"SOW","File");
        sow.setPropertyValue("file:content", (Serializable) rendered);
        session.createDocument(sow);
        return sow;
    }
}