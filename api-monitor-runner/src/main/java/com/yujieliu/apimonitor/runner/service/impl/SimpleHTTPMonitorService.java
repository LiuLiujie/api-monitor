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

import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.runner.service.MonitorService;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class SimpleHTTPMonitorService implements MonitorService<SimpleHTTPAPI, SimpleHTTPResult> {

    @Override
    public SimpleHTTPResult monitorAPI(SimpleHTTPAPI api) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .method(api.getRequest().getMethod(), null)
                .url(api.getRequest().getServer())
                .build();
        try(Response response = client.newCall(request).execute()){
            if (response.code() == api.getResponse().getStatus()){
                SimpleHTTPResult.getSuccessResult(api.getId(), true, new Date());
                return SimpleHTTPResult.getSuccessResult(api.getId(), true, new Date());
            }
            return SimpleHTTPResult.getSuccessResult(api.getId(), false, new Date());
        }catch (IOException e){
            return SimpleHTTPResult.getSuccessResult(api.getId(), false, new Date());
        }
    }
}
