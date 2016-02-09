package org.nuxeo.salesforce.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.util.Properties;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.salesforce.CreateSowOp;

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;

/**
 * Created by Michaël on 2/8/2016.
 */

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.template.manager.api",
        "org.nuxeo.template.manager",
        "org.nuxeo.template.manager.xdocreport",
        "nuxeo-salesforce-core",
        "studio.extensions.aws-sfdc"
})
public class TestCreateSowOp {

    @Inject
    CoreSession session;

    @Test
    public void TestRender() throws Exception {

        //create opportunity
        DocumentModel opportunity = session.createDocumentModel("/","Opportunity","Folder");
        opportunity.addFacet("salesforce");
        opportunity.setPropertyValue("sf:objectId","123");
        opportunity = session.createDocument(opportunity);
        session.save();

        //create template
        DocumentModel template = session.createDocumentModel("TemplateSource");
        File file = new File(getClass().getResource("/files/template.docx").getPath());
        Blob blob = new FileBlob(file);
        template.setPropertyValue("file:content", (Serializable) blob);
        template = session.createDocument(template);

        //render template
        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(template);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestCreateSowOp");

        Properties opportunityProps = new Properties();
        opportunityProps.put("objectId","123");
        opportunityProps.put("Name","My opportunity");

        Properties accountProps = new Properties();
        accountProps.put("Name","Nuxeo");
        accountProps.put("Name","My opportunity");
        accountProps.put("BillingStreet","46 Rene Clair");
        accountProps.put("BillingCity","Paris");
        accountProps.put("BillingPostalCode","75018");
        accountProps.put("BillingCountry","France");

        Properties userProps = new Properties();
        userProps.put("Name","Michael Vachette");
        userProps.put("Title","Solution Architect");
        userProps.put("Phone","+33 0 01 02 03 04");
        userProps.put("MobilePhone","+33 0 01 02 03 04");
        userProps.put("Email","mvachette@nuxeo.com");

        chain.add(CreateSowOp.ID).
                set("owner",userProps).
                set("account",accountProps).
                set("opportunity",opportunityProps);

        DocumentModel rendered = (DocumentModel) as.run(ctx, chain);

        Assert.assertNotNull(rendered);

    }

}
