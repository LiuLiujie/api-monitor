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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.o2r.mq.KafkaAPIPublisher;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class KafkaService<T extends BaseAPI> implements KafkaAPIPublisher<T> {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendAPIToRunner(T api) {
       var mapper = new ObjectMapper();
       try {
           String json = mapper.writeValueAsString(api);
           kafkaTemplate.send(api.getSchema(), json);
       } catch (JsonProcessingException e){
           log.error(e);
            //ignore
       }
    }
}
