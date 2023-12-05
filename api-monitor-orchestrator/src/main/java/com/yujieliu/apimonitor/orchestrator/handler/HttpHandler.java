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

package com.yujieliu.apimonitor.orchestrator.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.communication.orchestration.http.*;
import com.yujieliu.apimonitor.communication.orchestration.http.dto.*;
import com.yujieliu.apimonitor.communication.response.RestResponseEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Log4j2
@Controller //Need this or the @Mapping won't work
@ConditionalOnProperty(value = "api-monitor.communication.http.enable", havingValue = "true")
public class HttpHandler<API extends BaseAPI, Result extends BaseResult> extends BaseHandler<API, Result>
        implements HttpOrchestrator<API, Result> {

    //TODO: Use Redis instead of map for these
    //maintain a pending list for each runner
    private final Map<String, List<API>> pendingAPIs = new HashMap<>();

    //<runner, token>: the runnerId is from runner, token is issued by the orchestrator for auth
    private final Map<String, String> registeredRunners = new HashMap<>();

    private static final String registerToken = UUID.randomUUID().toString();

    @PostConstruct
    void printRegisterToken(){
        log.info("The register token for runner is: {}", HttpHandler.registerToken);
    }

    @Override
    @PostMapping(O2RConstant.HTTP_REGISTER_API)
    @ResponseBody
    public RestResponseEntity<Object> registerRunner(@RequestBody RegisterRequestBody body) {
        if (!HttpHandler.registerToken.equals(body.getRegisterToken()) || body.getRunnerId().isBlank()){
            return RestResponseEntity.sendBadOperation(O2RConstant.HTTP_REGISTER_ERROR);
        }
        String runnerId = body.getRunnerId();
        String token;
        if (this.registeredRunners.containsKey(runnerId)){
            token = registeredRunners.get(runnerId);
        }else{
            token = UUID.randomUUID().toString();
            registeredRunners.put(runnerId, token);
        }
        return RestResponseEntity.sendOK(new RegisterResponseBody(token));
    }

    @Override
    @PostMapping(O2RConstant.HTTP_HEARTBEAT_SIMPLE_HTTP_API)
    @ResponseBody
    public RestResponseEntity<Object> heartbeat(@RequestBody String bodyStr) {

        var mapper = new ObjectMapper();
        HeartbeatRequestBody<SimpleHTTPResult> body;
        try {
            body = mapper.readValue(bodyStr, new TypeReference<>() {});
        }catch (JsonProcessingException e){
            log.error(e);
            return RestResponseEntity.sendBadRequest(O2RConstant.HTTP_HEARTBEAT_ERROR);
        }

        String runnerId= body.getRunnerId();
        String token = body.getToken();

        if (runnerId.isBlank() || token.isBlank()){
            return RestResponseEntity.sendBadRequest(O2RConstant.HTTP_HEARTBEAT_ERROR);
        }

        if (!registeredRunners.containsKey(runnerId) || !registeredRunners.get(runnerId).equals(token)){
            return RestResponseEntity.sendBadOperation(O2RConstant.HTTP_AUTH_FAILURE);
        }

        //Handle the results, need to remove the apiId from the pending list
        if (!body.getResults().isEmpty()){
            if (pendingAPIs.containsKey(runnerId)){
                List<API> apis = pendingAPIs.get(runnerId);
                for (SimpleHTTPResult result : body.getResults()){
                    this.receiveResultFromRunner((Result) result);
                    apis.removeIf(api -> api.getId().equals(result.getApiId()));
                }
            } else {
                for (SimpleHTTPResult result : body.getResults()){
                    this.receiveResultFromRunner((Result) result);
                }
            }
        }

        if (body.getStatus() == RunnerStatusEnum.BUSY || !pendingAPIs.containsKey(runnerId)){
            return RestResponseEntity.sendOK(new HeartbeatResponseBody<>(new LinkedList<>()));
        }

        List<API> apis = new LinkedList<>(pendingAPIs.get(runnerId));
        log.info("Http receive a heartbeat from runner, id: {}, results: {}, new APIs: {}",
                runnerId, body.getResults().size(), apis.size());
        return RestResponseEntity.sendOK(new HeartbeatResponseBody<>(apis));
    }

    @Override
    public void sendAPIToRunner(API api) {
        for (String runnerId: registeredRunners.keySet()){
            if (pendingAPIs.containsKey(runnerId)){
                pendingAPIs.get(runnerId).add(api);
            }else{
                List<API> apis = new LinkedList<>();
                apis.add(api);
                pendingAPIs.put(runnerId, apis);
            }
        }
    }

    @Override
    public void receiveResultFromRunner(Result result) {
        super.addResult(result.getApiId(), result);
    }
}
