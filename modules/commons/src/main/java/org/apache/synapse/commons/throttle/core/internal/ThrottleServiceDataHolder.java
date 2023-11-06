/*
* Copyright 2005,2006 WSO2, Inc. http://wso2.com
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
*/
package org.apache.synapse.commons.throttle.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.commons.throttle.core.DistributedCounterManager;
import org.apache.synapse.commons.throttle.core.ThrottleProperties;
import org.apache.synapse.commons.throttle.core.ThrottleUtil;

import java.util.HashMap;
import java.util.Map;

public class ThrottleServiceDataHolder {
	private static final Log log  = LogFactory.getLog(ThrottleServiceDataHolder.class.getName());
	private Map<String,DistributedCounterManager> distributedCounterManagerMap = new HashMap<>();
	private Map<String,DistributedThrottleProcessor> distributedThrottleProcessorMap = new HashMap<>();

	private ThrottleServiceDataHolder() {

	}

	public ThrottleProperties getThrottleProperties() {
		return throttleProperties;
	}

	public void setThrottleProperties(ThrottleProperties throttleProperties) {
		this.throttleProperties = throttleProperties;
	}

	private ThrottleProperties throttleProperties = null;
	private static ThrottleServiceDataHolder thisInstance = new ThrottleServiceDataHolder();

	public static ThrottleServiceDataHolder getInstance() {
		if (thisInstance != null && thisInstance.getThrottleProperties() == null) {
			thisInstance.setThrottleProperties(
					ThrottleUtil.loadThrottlePropertiesFromConfigurations());
		}
		return thisInstance;
	}

	public void addDistributedCounterManager(DistributedCounterManager distributedCounterManager){
		distributedCounterManagerMap.put(distributedCounterManager.getType(),distributedCounterManager);
	}
	public void removeDistributedCounterManager(DistributedCounterManager distributedCounterManager){
		if (distributedCounterManager != null){
			distributedCounterManagerMap.remove(distributedCounterManager.getType());
		}
	}

	public DistributedCounterManager getDistributedCounterManager() {

		String distributedCounterType = throttleProperties.getDistributedCounterType();
		return distributedCounterManagerMap.get(distributedCounterType);
	}

	public void addDistributedThrottleProcessor(DistributedThrottleProcessor distributedThrottleProcessor) {
		distributedThrottleProcessorMap.put(distributedThrottleProcessor.getType(), distributedThrottleProcessor);
	}

	public void removeDistributedThrottleProcessor(DistributedThrottleProcessor distributedThrottleProcessor) {
		if (distributedThrottleProcessor != null) {
			distributedThrottleProcessorMap.remove(distributedThrottleProcessor.getType());
		}
	}

	public DistributedThrottleProcessor getDistributedThrottleProcessor() {
		String distributedThrottleProcessorType = throttleProperties.getDistributedThrottleProcessorType();
		return distributedThrottleProcessorMap.get(distributedThrottleProcessorType);
	}

}
