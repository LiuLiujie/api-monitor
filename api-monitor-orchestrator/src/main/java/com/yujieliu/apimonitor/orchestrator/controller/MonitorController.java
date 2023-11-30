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

import com.yujieliu.apimonitor.communication.domains.SimpleHTTPAPI;
import com.yujieliu.apimonitor.orchestrator.handler.MonitoringHandler;
import com.yujieliu.apimonitor.communication.response.RestResponseEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
public class MonitorController {

   @Value("${api-monitor.role}")
   private String mode = "standalone";

   @Resource
   List<MonitoringHandler> monitoringHandlers;

   @PostMapping("/monitor/http")
   public RestResponseEntity<Object> monitorApi(@RequestBody SimpleHTTPAPI api){
      boolean success = false;
      for (MonitoringHandler handler : monitoringHandlers){
         success = success || handler.addAPI(api);
      }
      if (!success){
         return RestResponseEntity.sendBadOperation("");
      }
      return RestResponseEntity.sendOK(null);
   }

   @PostConstruct
   private void initLocalRunner(){
      switch (mode) {
         case "standalone":
            for (MonitoringHandler handler : monitoringHandlers){
               handler.initLocalRunner();
            }
      }
      mockData();
   }

   private void mockData() {
      this.monitorApi(new SimpleHTTPAPI("GitHub API", "Simple-1.0",
              "https://api.github.com", "GET", 200));
   }
}
