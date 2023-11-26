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

package com.yujieliu.apimonitor.controller;

import com.yujieliu.apimonitor.domains.BaseResult;
import com.yujieliu.apimonitor.domains.SimpleResult;
import com.yujieliu.apimonitor.services.SimpleAPIMonitoringService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class MonitorController {

   @Resource
   SimpleAPIMonitoringService simpleAPIMonitoringService;

   @PostConstruct
   void monitoring(){
      log.info("Starting Application");
      simpleAPIMonitoringService.loadAPIs();
      List<BaseResult> results =  simpleAPIMonitoringService.monitoring();
      for (BaseResult result : results){
         if (result instanceof SimpleResult){
            log.info("Connection: {}", result.isConnection());
         }
      }
      log.info("Closing Application");
   }
}
