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

package com.yujieliu.apimonitor.orchestrator.service;

import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public abstract class BaseService<API extends BaseAPI, Result extends BaseResult> {

    //TODO: Use Redis to cache the result in the future
    private final Map<String, List<Result>> results = new ConcurrentHashMap<>();

    abstract public boolean addAPI(API api);

    public synchronized void addResult(String id, Result result){
        log.debug("Result added, id: {}, conn: {}, time: {}", id, result.isConnection(), result.getTime());
        if (results.containsKey(id)){
            List<Result> resultList = results.get(id);
            resultList.add(result);
        } else {
            List<Result> resultList = new LinkedList<>();
            resultList.add(result);
            results.put(id, resultList);
        }
    }
}
