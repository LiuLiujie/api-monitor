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

package com.yujieliu.apimonitor.orchestrator.controller;

import com.yujieliu.apimonitor.communication.constant.C2OConstant;
import com.yujieliu.apimonitor.communication.domains.BaseAPI;
import com.yujieliu.apimonitor.communication.domains.BaseResult;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.communication.domains.SimpleHTTPResult;
import com.yujieliu.apimonitor.orchestrator.handler.APIHandlerInterface;
import com.yujieliu.apimonitor.communication.response.RestResponseEntity;
import com.yujieliu.apimonitor.orchestrator.service.BaseService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
public class MonitorController {

   @Resource(type = BaseService.class)
   private BaseService<BaseAPI, BaseResult> monitoringService;

   @Resource
   List<APIHandlerInterface<SimpleHTTPAPI, SimpleHTTPResult>> httpHandlers;

   @PostMapping("/monitor/http")
   public RestResponseEntity<Object> monitorApi(@RequestBody String jsonStr){
      log.info(jsonStr);
      SimpleHTTPAPI api = null;
      for (var handler: httpHandlers){
         api = handler.constrcutAPI(jsonStr);
      }
      if (api == null){
         return RestResponseEntity.sendBadOperation(C2OConstant.INVALID_HTTP_API);
      }

      boolean success = monitoringService.addAPI(api);
      if (!success){
         return RestResponseEntity.sendBadOperation(C2OConstant.ADDING_API_ERROR);
      }
      Map<String, String> data = new HashMap<>(1);
      data.put("id", api.getId());
      return RestResponseEntity.sendOK(data);
   }
}
