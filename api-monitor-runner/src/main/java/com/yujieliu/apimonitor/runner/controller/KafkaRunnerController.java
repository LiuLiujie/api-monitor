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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.o2r.mq.KafkaAPISubscriber;
import com.yujieliu.apimonitor.communication.o2r.mq.KafkaResultPublisher;
import com.yujieliu.apimonitor.runner.handler.SimpleHTTPMonitor;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
public class KafkaRunnerController<API extends BaseAPI, Result extends BaseResult>
        extends RunnerController<API, Result>
        implements KafkaAPISubscriber<API>, KafkaResultPublisher<Result> {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void receiveAPIFromOrchestrator(API api) {
        if (api instanceof SimpleHTTPAPI){
            var result = new SimpleHTTPMonitor().monitorAPI((SimpleHTTPAPI) api);
            result.setApi(api);
            this.sendResultToOrchestrator((Result) result);
        }
    }

    @Override
    public void sendResultToOrchestrator(Result result) {
        try {
            String jsonStr = new ObjectMapper().writeValueAsString(result);
            kafkaTemplate.send(O2RConstant.HTTP_RESULT_TOPIC, result.getApi().getId(), jsonStr);
        } catch (JsonProcessingException e){
            kafkaTemplate.send(O2RConstant.RUNNER_ERROR_TOPIC, result.getApi().getId(), O2RConstant.RESULT_ERROR);
        }
    }

    @KafkaListener(topics = O2RConstant.HTTP_API_TOPIC, groupId = "${api-monitor.runner.id}")
    private void receiveHttpAPIFromMQ(ConsumerRecord<String, String> consumerRecord){
        String id = consumerRecord.key();
        String jsonStr = consumerRecord.value();
        log.info("Kafka Receive API, Id: {}", id);
        try {
            API api = (API) new ObjectMapper().readValue(jsonStr, SimpleHTTPAPI.class);
            this.receiveAPIFromOrchestrator(api);
        } catch (JsonProcessingException e){
            kafkaTemplate.send(O2RConstant.RUNNER_ERROR_TOPIC, id, O2RConstant.API_ERROR);
        }
    }
}
