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

package com.yujieliu.apimonitor.orchestrator.service.impl;

import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.orchestrator.service.APIService;
import com.yujieliu.apimonitor.orchestrator.handler.BaseHandler;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class SimpleHttpAPIService implements APIService<SimpleHTTPAPI, SimpleHTTPResult> {

    @Resource
    private List<BaseHandler<SimpleHTTPAPI, SimpleHTTPResult>> simpleHttpHandlers;

    @Override
    public String addAPI(SimpleHTTPAPI api) {
        String apiId = UUID.randomUUID().toString();
        api.setId(apiId);
        for (var handler: simpleHttpHandlers){
            handler.sendAPIToRunner(api);
        }
        return apiId;
    }

    @Override
    public List<SimpleHTTPResult> getAPIResults(String apiId) {
        List<SimpleHTTPResult> results = new LinkedList<>();
        for (var handler: simpleHttpHandlers){
            results.addAll(handler.getResults(apiId));
        }
        return results;
    }
}
