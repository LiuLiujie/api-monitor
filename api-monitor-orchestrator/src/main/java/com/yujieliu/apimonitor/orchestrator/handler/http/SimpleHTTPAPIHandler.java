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

package com.yujieliu.apimonitor.orchestrator.handler.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.orchestrator.handler.APIHandlerInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Order(1)
@Component
@Log4j2
public class SimpleHTTPAPIHandler implements APIHandlerInterface<SimpleHTTPAPI, SimpleHTTPResult> {
    @Override
    public SimpleHTTPAPI constrcutAPI(String jsonStr) {
        var mapper = new ObjectMapper();
        try {
            String id = UUID.randomUUID().toString();
            SimpleHTTPAPI api = mapper.readValue(jsonStr, SimpleHTTPAPI.class);
            api.setId(id);
            return api;
        }catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public SimpleHTTPResult constructResult(String jsonStr) {
        return null;
    }
}
