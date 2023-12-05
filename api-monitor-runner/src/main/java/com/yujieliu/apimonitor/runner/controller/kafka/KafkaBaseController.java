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

package com.yujieliu.apimonitor.runner.controller.kafka;

import com.yujieliu.apimonitor.communication.constant.O2RConstant;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.orchestration.mq.KafkaRunner;
import com.yujieliu.apimonitor.runner.service.MonitorService;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class KafkaBaseController<API extends BaseAPI, Result extends BaseResult>
        implements KafkaRunner<API, Result> {

    @Resource
    KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    MonitorService<API, Result> monitorService;

    abstract void receiveAPIFromMQ(ConsumerRecord<String, String> consumerRecord);

    public void receiveAPIFromOrchestrator(API api) {
        var result = monitorService.monitorAPI(api);
        result.setApiId(api.getId());
        sendResultToOrchestrator(result);
    }

    public void sendError(String apiId, String errorInfo){
        kafkaTemplate.send(O2RConstant.RUNNER_ERROR_TOPIC, apiId, errorInfo);
    }
}
