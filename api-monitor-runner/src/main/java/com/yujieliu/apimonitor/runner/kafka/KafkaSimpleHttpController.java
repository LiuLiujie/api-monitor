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

package com.yujieliu.apimonitor.runner.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@ConditionalOnProperty(value = "apiId-monitor.kafka.enable-kafka", havingValue = "true")
public class KafkaSimpleHttpController extends KafkaBaseController<SimpleHTTPAPI, SimpleHTTPResult> {

    @Override
    public void sendResultToOrchestrator(SimpleHTTPResult result) {
        try {
            String jsonStr = new ObjectMapper().writeValueAsString(result);
            this.kafkaTemplate.send(O2RConstant.SIMPLE_HTTP_RESULT_TOPIC, result.getApiId(), jsonStr);
        } catch (JsonProcessingException e){
            this.sendError(result.getApiId(), O2RConstant.RESULT_ERROR);
        }
    }

    @Override
    @KafkaListener(topics = O2RConstant.SIMPLE_HTTP_API_TOPIC, groupId = "${api-monitor.runner.id}")
    protected void receiveAPIFromMQ(ConsumerRecord<String, String> consumerRecord){
        String id = consumerRecord.key();
        String jsonStr = consumerRecord.value();
        log.info("Kafka Receive SimpleHttp API, Id: {}", id);
        try {
            var api = new ObjectMapper().readValue(jsonStr, SimpleHTTPAPI.class);
            this.receiveAPIFromOrchestrator(api);
        } catch (JsonProcessingException e){
            this.sendError(id, O2RConstant.API_ERROR);
        }
    }
}
