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

import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.communication.standalone.APISubscriber;
import com.yujieliu.apimonitor.communication.standalone.ResultSubscriber;
import com.yujieliu.apimonitor.runner.handler.SimpleHTTPHandler;

public class SimpleHTTPController implements APISubscriber<SimpleHTTPAPI> {

    private final SimpleHTTPHandler handler = new SimpleHTTPHandler();

    public SimpleHTTPController(ResultSubscriber<SimpleHTTPResult> subscriber){
        handler.addSubscriber(subscriber);
    }

    @Override
    public void sendAPIToSubscriber(SimpleHTTPAPI api) {
        handler.monitorAPI(api);
    }
}
