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


import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;

public class BaseHandler<API extends BaseAPI, Result extends BaseResult> implements MonitorInterface<API, Result> {

    private MonitorInterface<API, Result> next;

    @Override
    public void setNext(MonitorInterface<API, Result> handler) {
        this.next = handler;
    }

    @Override
    public Result monitorAPI(API api) {
        return next.monitorAPI(api);
    }
}
