package org.nuxeo.salesforce.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

/**
 * Created by Michaël on 2/8/2016.
 */

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({"nuxeo-salesforce-core"})
public class TestExtendedAuditLogOp {

    @Inject
    CoreSession session;

    @Test
    public void TestAudit() throws Exception {

        //create opportunity
        /*DocumentModel opportunity = session.createDocumentModel("/", "File", "File");
        opportunity = session.createDocument(opportunity);
        session.save();

        //add log entry
        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(opportunity);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestExtendedAuditLogOp");


        chain.add(ExtendedAuditLogOp.ID).
                set("event", "myEvent").
                set("category", "myCategory").
                set("comment", "myComment").
                set("pageNumber",3).
                set("timeSpentSeconds",15);

        opportunity = (DocumentModel) as.run(ctx, chain);*/
    }

}
