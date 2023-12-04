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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public abstract class BaseRedisCacheHandler<API extends BaseAPI, Result extends BaseResult> extends BaseHandler<API, Result> {

    //Redis expire the key (the results of the APIs)
    @Value("${api-monitor.cache.expiration}")
    private int expiration;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public synchronized void addResult(String apiId, Result result) {
        log.info("Redis handler tries to save result id: {}, time: {}", result.getApiId(), result.getTime());
        try {
            String jsonStr = new ObjectMapper().writeValueAsString(result);
            stringRedisTemplate.opsForList().rightPush(apiId, jsonStr);
            if (expiration > 0){
                stringRedisTemplate.expire(apiId, expiration, TimeUnit.SECONDS);
            }
        }catch (JsonProcessingException e){
            log.info("Write Result {} to redis cache fail: {}", apiId, e.getMessage());
        }
    }

    @Override
    public List<Result> getResults(String apiId) {
        List<String> strResults = stringRedisTemplate.opsForList().range(apiId, 0, -1);
        if (strResults == null){
            return new LinkedList<>();
        }
        List<Result> results = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String strResult : strResults){
            try {
                Result result = mapper.readValue(strResult, new TypeReference<>() {});
                results.add(result);
            }catch (JsonProcessingException e){
                log.info("Read Result {} from redis cache fail: {}", apiId, e.getMessage());
            }
        }
        return results;
    }
}
