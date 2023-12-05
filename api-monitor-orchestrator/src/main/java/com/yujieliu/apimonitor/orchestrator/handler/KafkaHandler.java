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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.communication.orchestration.mq.KafkaOrchestrator;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@ConditionalOnProperty(value = "api-monitor.communication.kafka.enable", havingValue = "true")
public class KafkaHandler<API extends BaseAPI, Result extends BaseResult> extends BaseHandler<API, Result>
        implements KafkaOrchestrator<API, Result> {

    //TODO: Use Redis instead of map
    private final Map<String, API> sentAPI = new HashMap<>();

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendAPIToRunner(API api) {
        try {
            String jsonStr = new ObjectMapper().writeValueAsString(api);
            if (api instanceof SimpleHTTPAPI){
                kafkaTemplate.send(O2RConstant.SIMPLE_HTTP_API_TOPIC, api.getId(), jsonStr);
                sentAPI.put(api.getId(), api);
            }
        } catch (JsonProcessingException e){
        }
    }

    @Override
    public void receiveResultFromRunner(Result result) {
        super.addResult(result.getApiId(), result);
    }

    @Bean
    public KafkaAdmin.NewTopics createTopic() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(O2RConstant.SIMPLE_HTTP_RESULT_TOPIC)
                        .build(),
                TopicBuilder.name(O2RConstant.SIMPLE_HTTP_API_TOPIC)
                        .build(),
                TopicBuilder.name(O2RConstant.RUNNER_ERROR_TOPIC)
                        .build()
        );
    }

    /**
     * Consume the result from runners one by one, need to update
     * //TODO: Update to consumer in batch using kafka batchFactory
     * @param consumerRecord Results from runners
     */
    @KafkaListener(topics = O2RConstant.SIMPLE_HTTP_RESULT_TOPIC, groupId = "orchestrator")
    private void receiveSimpleHttpResultFromRunner(ConsumerRecord<String, String> consumerRecord){

        String id = consumerRecord.key();
        String jsonStr = consumerRecord.value();
        log.info("Kafka receive result, id: {}", id);
        try {
            Result result = (Result) new ObjectMapper().readValue(jsonStr, SimpleHTTPResult.class);
            receiveResultFromRunner(result);
            sentAPI.remove(id);
        } catch (JsonProcessingException e){
            receiveResultFromRunner((Result) SimpleHTTPResult.getFailureResult(id, new Date()));
        }
    }
}
