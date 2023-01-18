package com.liveQIQI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.liveQIQI")
public class LiveBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveBackendApplication.class, args);
    }

}
