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

import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.o2r.standalone.StandaloneOrchestrator;
import com.yujieliu.apimonitor.runner.standalone.StandaloneRunnerController;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(value = "api-monitor.role", havingValue = "standalone")
public class StandaloneHandler<API extends BaseAPI, Result extends BaseResult> extends BaseHandler<API, Result>
        implements StandaloneOrchestrator<API, Result> {

    StandaloneRunnerController<API, Result> controller = new StandaloneRunnerController<>(this);

    @Override
    public void sendAPIToRunner(API api) {
        controller.receiveAPIFromOrchestrator(api);
    }

    @Override
    public void receiveResultFromRunner(Result result) {
        log.info("Standalone Handler Receive API result, id: {}", result.getApiId());
        super.addResult(result.getApiId(), result);
    }
}
