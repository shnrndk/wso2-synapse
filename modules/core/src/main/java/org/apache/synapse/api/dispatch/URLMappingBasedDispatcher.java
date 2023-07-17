/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.synapse.api.dispatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.api.ApiUtils;
import org.apache.synapse.api.Resource;
import org.apache.synapse.rest.RESTConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class URLMappingBasedDispatcher implements RESTDispatcher {

    private static final Log log = LogFactory.getLog(URLMappingBasedDispatcher.class);

    public Resource findResource(MessageContext synCtx, Collection<Resource> resources) {
        List<URLMappingHelper> mappings = new ArrayList<URLMappingHelper>();
        List<Resource> filteredResources = new ArrayList<Resource>();

        for (Resource r : resources) {
            DispatcherHelper helper = r.getDispatcherHelper();
            if (helper instanceof URLMappingHelper) {
                mappings.add((URLMappingHelper) helper);
                filteredResources.add(r);
            }
        }

        int count = filteredResources.size();
        if (count == 0) {
            return null;
        }

        String url = ApiUtils.getSubRequestPath(synCtx);
        for (int i = 0; i < count; i++) {
            if (mappings.get(i).isExactMatch(url)) {
                if (log.isDebugEnabled()) {
                    log.debug("Found exact URL match for: " + url);
                }
                synCtx.setProperty(RESTConstants.SELECTED_RESOURCE, filteredResources.get(i));
                return filteredResources.get(i);
            }
        }

        int maxLength = 0;
        Resource matchedResource = null;
        for (int i = 0; i < count; i++) {
            int length = mappings.get(i).getPrefixMatchingLength(url);
            if (length > maxLength) {
                maxLength = length;
                matchedResource = filteredResources.get(i);
            }
        }
        if (matchedResource != null) {
            if (log.isDebugEnabled()) {
                log.debug("Found path match for: " + url + " with matching length: " + maxLength);
            }
            synCtx.setProperty(RESTConstants.SELECTED_RESOURCE, matchedResource);
            return matchedResource;
        }

        for (int i = 0; i < count; i++) {
            if (mappings.get(i).isExtensionMatch(url)) {
                if (log.isDebugEnabled()) {
                    log.debug("Found extension match for: " + url);
                }
                synCtx.setProperty(RESTConstants.SELECTED_RESOURCE, filteredResources.get(i));
                return filteredResources.get(i);
            }
        }

        return null;
    }
}
