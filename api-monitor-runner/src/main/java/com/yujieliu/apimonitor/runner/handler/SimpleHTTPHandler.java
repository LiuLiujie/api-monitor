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

package com.yujieliu.apimonitor.runner.handler;

import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.communication.standalone.ResultPublisher;
import com.yujieliu.apimonitor.communication.standalone.ResultSubscriber;
import okhttp3.*;

import java.io.IOException;
import java.util.Date;

public class SimpleHTTPHandler extends BaseHandler implements ResultPublisher<SimpleHTTPResult> {

    private ResultSubscriber<SimpleHTTPResult> subscriber;

    public void monitorAPI(SimpleHTTPAPI api) {
        OkHttpClient client = new OkHttpClient();
        Request result = new Request.Builder()
                .method(api.getRequest().getMethod(), null)
                .url(api.getRequest().getServer())
                .build();

        Call call = client.newCall(result);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                notifyResultSubscribers(new SimpleHTTPResult(false, new Date()));
            }
            @Override
            public void onResponse(Call call, final Response response) {
                if (response!=null){
                    if (response.code() == api.getResponse().getStatus()){
                        notifyResultSubscribers(new SimpleHTTPResult(true, new Date()));
                        return;
                    }
                }
                notifyResultSubscribers(new SimpleHTTPResult(false, new Date()));
            }
        });
    }

    @Override
    public void addSubscriber(ResultSubscriber<SimpleHTTPResult> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void notifyResultSubscribers(SimpleHTTPResult result) {
        this.subscriber.sendResultToOrchestrator(result);
    }
}
