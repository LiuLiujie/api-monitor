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

package com.yujieliu.apimonitor.runner.http;

import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.communication.o2r.http.dto.HeartbeatRequestBody;
import com.yujieliu.apimonitor.communication.o2r.http.dto.HeartbeatResponseBody;
import com.yujieliu.apimonitor.communication.o2r.http.dto.RunnerStatusEnum;
import com.yujieliu.apimonitor.communication.response.Code;
import com.yujieliu.apimonitor.communication.response.RestResponseEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Log4j2
@Controller
@ConditionalOnProperty(value = "api-monitor.http.enable-rest-api", havingValue = "true")
public class HttpSimpleHttpController extends HttpBaseController<SimpleHTTPAPI, SimpleHTTPResult> {

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void heartbeat() {
        String URL = this.orchestrator+ O2RConstant.HTTP_HEARTBEAT_SIMPLE_HTTP_API;
        //Send the results
        var request = new HeartbeatRequestBody<>(runnerId, token, RunnerStatusEnum.OK, this.results);
        ParameterizedTypeReference<RestResponseEntity<HeartbeatResponseBody<SimpleHTTPAPI>>> typeRef = new ParameterizedTypeReference<>() {};
        var body = this.getTemplate().exchange(URL, HttpMethod.POST, new HttpEntity<>(request), typeRef).getBody();

        if (body == null || body.getCode() == Code.BAD_REQUEST || body.getCode() == Code.BAD_OPERATION){
            log.error("Heartbeat Simple API fail: {}", body != null ? body.getMsg() : "No Response Body");
            return;
        }
        log.info("Heartbeat send {} Simple API results and {} new APIs", this.results.size(), body.getData().getApis().size());
        this.results.clear();
        //Handle the new APIs
        for (var api : body.getData().getApis()){
            receiveAPIFromOrchestrator(api);
        }
    }
}
