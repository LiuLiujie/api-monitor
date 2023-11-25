package com.yujieliu.apimonitor.services;

import com.yujieliu.apimonitor.domains.BaseAPI;
import com.yujieliu.apimonitor.domains.BaseResult;
import com.yujieliu.apimonitor.domains.SimpleAPI;
import com.yujieliu.apimonitor.domains.SimpleResult;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class SimpleAPIMonitoringService extends BaseMonitoringService {

    @Resource
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    public void loadAPIs() {
        //TODO
        super.getApis().add(new SimpleAPI("GitHub API", "Simple-1.0", "https://api.github.com", "GET", 200));
        log.info("Load {} Simple APIs", super.getApis().size());
    }

    @Override
    public List<BaseResult> monitoring() {
        List<BaseResult> results = new ArrayList<>(super.getApis().size());
        for (BaseAPI baseApi : super.getApis()){
            if (!baseApi.getSchema().startsWith("Simple")){
                continue;
            }
            SimpleAPI api = (SimpleAPI) baseApi;
            String url = api.getRequest().getServer();
            log.info("Start monitoring {}", api.getName());
            ResponseEntity<String> response = this.getTemplate().exchange(url, HttpMethod.GET, null, String.class);
            var httpStatusCode = response.getStatusCode();
            if (httpStatusCode.is2xxSuccessful()){
                results.add(new SimpleResult(true, new Date()));
            }
        }
        return results;
    }

    private RestTemplate getTemplate(){
        return this.restTemplateBuilder
                .build();
    }
}
