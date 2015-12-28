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

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.api.Framework;

/**
 * Endpoint used to retrieve informations from OAuth2 Provider directory (except crucial ones).
 *
 * @since 7.4
 */
@Path("/api/v1/oauth2directory")
@Produces("application/json")
@WebObject(type = "oauth2directory")
public class OAuth2DirectoryObject extends DefaultObject {

    public static final String OAUTH2CLIENT_DIRECTORY_NAME = "oauth2ServiceProviders";

    public static final String USER_AUTHORIZATION_URL = "userAuthorizationURL";

    public static final String CLIENT_ID = "clientId";

    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
    }

    @GET
    @Path("{serviceName}")
    public Object doGetOAuth2ProviderInformations(@PathParam("serviceName") String serviceName) throws IOException {
        DirectoryService service = Framework.getLocalService(DirectoryService.class);
        try (Session session = service.open(OAUTH2CLIENT_DIRECTORY_NAME)) {
            for (DocumentModel entry : session.getEntries()) {
                String name = (String) entry.getPropertyValue("oauth2ServiceProvider:serviceName");
                if (serviceName.equals(name)) {
                    ObjectMapper mapper = new ObjectMapper();
                    StringWriter writer = new StringWriter();
                    Map<String, String> values = new HashMap<>();
                    values.put(CLIENT_ID, (String) entry.getProperty("oauth2ServiceProvider", CLIENT_ID));
                    values.put(USER_AUTHORIZATION_URL,
                            (String) entry.getProperty("oauth2ServiceProvider" + "", USER_AUTHORIZATION_URL));
                    mapper.writeValue(writer, values);
                    return writer.toString();
                }
            }
        }
        return null;
    }

}
