/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 */

package org.nuxeo.salesforce;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import com.google.common.base.Strings;

/**
 * Enricher for Audit LogEntries.
 *
 * @since 7.10
 */
@Setup(mode = SINGLETON, priority = REFERENCE)
public class LogEntryEnricher extends AbstractJsonEnricher<LogEntry> {

    public static final String NAME = "sfLogEntry";

    public LogEntryEnricher() {
        super(NAME);
    }

    @Override
    public void write(JsonGenerator jsonGenerator, LogEntry logEntry) throws IOException {
        CoreSession session = CoreInstance.openCoreSession(logEntry.getRepositoryId());
        DocumentModel targetDocument = session.getDocument(new IdRef(logEntry.getDocUUID()));
        Date creationDate = ((GregorianCalendar) targetDocument.getPropertyValue("dc:created")).getTime();
        Date modificationDate = ((GregorianCalendar) targetDocument.getPropertyValue("dc:modified")).getTime();

        jsonGenerator.writeObjectFieldStart(NAME);
        jsonGenerator.writeStringField("contact", getPrincipalName(logEntry));
        jsonGenerator.writeStringField("creation", new SimpleDateFormat("yyyy-MM-dd HH:mm a").format(creationDate));
        jsonGenerator.writeStringField("modification", new SimpleDateFormat("yyyy-MM-dd HH:mm a").format(modificationDate));
        jsonGenerator.writeEndObject();
    }

    protected String getPrincipalName(LogEntry logEntry) {
        UserManager userManager = Framework.getLocalService(UserManager.class);
        String principalName = logEntry.getPrincipalName();
        NuxeoPrincipal principal = userManager.getPrincipal(principalName);
        if (principal == null) {
            return principalName;
        }
        String firstName = principal.getFirstName();
        String lastName = principal.getLastName();
        if (Strings.isNullOrEmpty(firstName) && Strings.isNullOrEmpty(lastName)) {
            return principal.getName();
        } else {
            return firstName + " " + lastName;
        }
    }
}
