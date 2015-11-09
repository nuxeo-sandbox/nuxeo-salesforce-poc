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
        jsonGenerator.writeObjectField("contact", getPrincipalName(logEntry));
        jsonGenerator.writeObjectField("creation", new SimpleDateFormat("yyyy-MM-dd").format(creationDate));
        jsonGenerator.writeObjectField("modification", new SimpleDateFormat("yyyy-MM-dd").format(modificationDate));
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
