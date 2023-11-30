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
import com.yujieliu.apimonitor.communication.standalone.ResultSubscriber;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.standalone.APIPublisher;
import com.yujieliu.apimonitor.communication.standalone.APISubscriber;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.runner.controller.SimpleHTTPController;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Order(1)
@Component
@Log4j2
public class MonitorSimpleHTTPAPI implements APIPublisher<SimpleHTTPAPI>, ResultSubscriber<SimpleHTTPResult>,
        MonitoringHandler {

    private final List<APISubscriber<SimpleHTTPAPI>> subscribers = new ArrayList<>();

    @Override
    public boolean addAPI(BaseAPI api) {
        if (api instanceof SimpleHTTPAPI){
            notifyAPISubscribers((SimpleHTTPAPI) api);
            return true;
        }
        return false;
    }

    @Override
    public void sendResultToOrchestrator(SimpleHTTPResult result) {
        //TODO: handler results
        System.out.println("Result: "+result.isConnection()+ " time: "+result.getTime());
    }

    @Override
    public void initLocalRunner() {
        this.subscribers.add(new SimpleHTTPController(this));
    }

    @Override
    public void addSubscriber(APISubscriber<SimpleHTTPAPI> subscriber) {
        this.subscribers.add(subscriber);
    }

    @Override
    public void notifyAPISubscribers(SimpleHTTPAPI api) {
        for (APISubscriber<SimpleHTTPAPI> subscriber : subscribers){
            subscriber.sendAPIToSubscriber(api);
        }
    }
}
