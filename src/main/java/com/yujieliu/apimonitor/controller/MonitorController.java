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
