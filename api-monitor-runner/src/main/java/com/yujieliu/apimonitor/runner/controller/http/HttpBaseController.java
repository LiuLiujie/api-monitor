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

package com.yujieliu.apimonitor.runner.controller.http;

import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.orchestration.http.HttpRunner;
import com.yujieliu.apimonitor.communication.orchestration.http.dto.RegisterRequestBody;
import com.yujieliu.apimonitor.communication.orchestration.http.dto.RegisterResponseBody;
import com.yujieliu.apimonitor.communication.response.Code;
import com.yujieliu.apimonitor.communication.response.RestResponseEntity;
import com.yujieliu.apimonitor.runner.controller.ConfigurableController;
import com.yujieliu.apimonitor.runner.service.ConfigService;
import com.yujieliu.apimonitor.runner.service.MonitorService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;


@Log4j2
public abstract class HttpBaseController<API extends BaseAPI, Result extends BaseResult>
        implements HttpRunner<API, Result>, ConfigurableController {

    @Value("${api-monitor.communication.rest-api.register-token}")
    protected String registerToken;

    @Value("${api-monitor.communication.rest-api.orchestrator}")
    protected String orchestrator;

    @Value("${api-monitor.runner.id}")
    protected String runnerId;

    @Resource
    private RestTemplateBuilder restTemplateBuilder;

    @Resource
    MonitorService<API, Result> monitorService;

    @Resource
    ConfigService configService = null;

    protected String token;

    LinkedList<Result> results = new LinkedList<>();

    @Override
    public void receiveAPIFromOrchestrator(API api) {
        var result =this.monitorService.monitorAPI(api);
        result.setApiId(api.getId());
        this.sendResultToOrchestrator(result);
    }

    @Override
    public void sendResultToOrchestrator(Result result) {
        this.results.add(result);
    }

    @Override
    public void loadConfig() {
        if (configService != null){
            String orchestrator = this.configService.getProperty("api-monitor.communication.rest-api.orchestrator");
            String registerToken = this.configService.getProperty("api-monitor.communication.rest-api.register-token");
            if (!orchestrator.isBlank() && !registerToken.isBlank()){
                this.orchestrator = orchestrator;
                this.registerToken = registerToken;
                log.info("Receive orchestrator info from configuration service, override the local ones");
            }
        }
    }

    @PostConstruct
    public void register() {
        //Try to load config from configuration service
        this.loadConfig();

        String url = this.orchestrator+ O2RConstant.HTTP_REGISTER_API;
        var request = new RegisterRequestBody(this.registerToken, this.runnerId);
        ParameterizedTypeReference<RestResponseEntity<RegisterResponseBody>> typeRef = new ParameterizedTypeReference<>() {};
        var body = this.getTemplate().exchange(url, HttpMethod.POST, new HttpEntity<>(request), typeRef).getBody();
        if (body == null){
            log.error("Registration fail, please check if the orchestrator is alive");
            System.exit(-1);
        }
        if (body.getCode() == Code.BAD_REQUEST || body.getCode() == Code.BAD_OPERATION){
            log.error("Registration fail, please check the parameters for registration");
            System.exit(-1);
        }

        token = body.getData().getToken();
        if (token == null || token.isEmpty()){
            log.error("Returned token from orchestrator is null, please check the orchestrator");
            System.exit(-1);
        }
        this.heartbeat();//Start the first heartbeat
    }

    protected RestTemplate getTemplate(){
        return this.restTemplateBuilder
                .build();
    }
}
