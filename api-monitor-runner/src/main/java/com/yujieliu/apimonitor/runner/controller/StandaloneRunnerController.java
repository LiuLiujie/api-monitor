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

package com.yujieliu.apimonitor.runner.controller;

import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.o2r.standalone.StandaloneOrchestrator;
import com.yujieliu.apimonitor.communication.o2r.standalone.StandaloneRunner;
import com.yujieliu.apimonitor.runner.handler.SimpleHTTPMonitor;

public class StandaloneRunnerController<API extends BaseAPI, Result extends BaseResult>
        extends RunnerController<API, Result>
        implements StandaloneRunner<API, Result> {

    private final StandaloneOrchestrator<API, Result> orchestrator;

    public StandaloneRunnerController(StandaloneOrchestrator<API, Result> orchestrator){
        this.orchestrator = orchestrator;
    }

    @Override
    public void receiveAPIFromOrchestrator(API api) {
        if (api instanceof SimpleHTTPAPI){
            var result = new SimpleHTTPMonitor().monitorAPI((SimpleHTTPAPI) api);
            this.sendResultToOrchestrator((Result) result);
        }
    }

    @Override
    public void sendResultToOrchestrator(Result result) {
        orchestrator.receiveResultFromRunner(result);
    }
}
