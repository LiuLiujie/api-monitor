/*
 *
 *    Copyright 2023 Yujie Liu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.yujieliu.apimonitor.runner.service.impl;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.yujieliu.apimonitor.runner.service.ConfigService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.util.Properties;

@Log4j2
@Service
public class NacosConfigService implements ConfigService {

    @Value("${api-monitor.config.nacos.server}")
    private String nacosServer;
    @Value("${api-monitor.config.nacos.username}")
    private String nacosUsername = "";
    @Value("${api-monitor.config.nacos.password}")
    private String nacosPassword = "";
    @Value("${api-monitor.config.nacos.group}")
    private String nacosGroup;
    @Value("${api-monitor.config.nacos.data-id}")
    private String nacosDataId;
    private final Properties request = new Properties();
    private final Properties properties = new Properties();

    @Override
    public String getProperty(String key) {
        try {
            String property = this.getContext();
            if (StringUtils.hasText(property)) {
                properties.load(new StringReader(property));
            }
        } catch (Exception e) {
            log.error("Nacos error:{}", ExceptionUtils.getStackTrace(e));
        }
        String property = properties.getProperty(key);
        return property != null? property : "";
    }

    private String getContext() {
        String context = null;
        try {
            request.put(PropertyKeyConst.SERVER_ADDR, nacosServer);
            if (nacosUsername != null && !nacosUsername.isBlank()){
                request.put(PropertyKeyConst.USERNAME, nacosUsername);
                request.put(PropertyKeyConst.PASSWORD, nacosPassword);
            }
            context = NacosFactory.createConfigService(request)
                    .getConfig(nacosDataId, nacosGroup, 5000);
        } catch (NacosException e) {
            log.error("Nacos error:{}", ExceptionUtils.getStackTrace(e));
        }
        return context;
    }
}
