/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
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

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.io.download.DownloadService;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.runtime.api.Framework;

/**
 * Enricher used to retrieve drive edit url.
 *
 * @since 7.10
 */
@Setup(mode = SINGLETON, priority = REFERENCE)
public class DriveEditEnricher extends AbstractJsonEnricher<DocumentModel> {

    public static final String NXDRIVE_PROTOCOL = "nxdrive";

    public static final String PROTOCOL_COMMAND_EDIT = "edit";

    public static final String NEW_DRIVE_EDIT_URL_PROP_KEY = "org.nuxeo.drive.new.edit.url";

    public static final String NAME = "driveEditURL";

    public DriveEditEnricher() {
        super(NAME);
    }

    @Override
    public void write(JsonGenerator jg, DocumentModel doc) throws IOException {
        jg.writeStringField(NAME, doGetDriveEditURL(doc));
    }

    protected String doGetDriveEditURL(DocumentModel currentDocument) throws IOException {
        CoreSession session = currentDocument.getCoreSession();
        BlobHolder bh = currentDocument.getAdapter(BlobHolder.class);
        if (bh == null) {
            return null;
        }
        Blob blob = bh.getBlob();
        if (blob == null) {
            return null;
        }
        String fileName = blob.getFilename();
        String baseURL = ctx.getBaseUrl();
        StringBuffer sb = new StringBuffer();
        sb.append(NXDRIVE_PROTOCOL).append("://");
        sb.append(PROTOCOL_COMMAND_EDIT).append("/");
        sb.append(baseURL.replaceFirst("://", "/"));
        if (Boolean.valueOf(Framework.getProperty(NEW_DRIVE_EDIT_URL_PROP_KEY))) {
            sb.append("user/");
            sb.append(session.getPrincipal().getName());
            sb.append("/");
        }
        sb.append("repo/");
        sb.append(session.getRepositoryName());
        sb.append("/nxdocid/");
        sb.append(currentDocument.getId());
        sb.append("/filename/");
        String escapedFilename = fileName.replaceAll("(/|\\\\|\\*|<|>|\\?|\"|:|\\|)", "-");
        sb.append(URIUtils.quoteURIPathComponent(escapedFilename, true));
        if (Boolean.valueOf(Framework.getProperty(NEW_DRIVE_EDIT_URL_PROP_KEY))) {
            sb.append("/downloadUrl/");
            DownloadService downloadService = Framework.getService(DownloadService.class);
            String downloadUrl = downloadService.getDownloadUrl(currentDocument, DownloadService.BLOBHOLDER_0, "");
            sb.append(downloadUrl);
        }
        return sb.toString();
    }

}
