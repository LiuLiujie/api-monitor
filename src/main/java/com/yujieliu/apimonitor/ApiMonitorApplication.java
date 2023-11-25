package com.yujieliu.apimonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ApiMonitorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ApiMonitorApplication.class, args);
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

}
