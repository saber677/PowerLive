package com.liveQIQI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.liveQIQI")
public class LiveBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveBackendApplication.class, args);
    }

}
