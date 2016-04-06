package com.philips.microservices.core.observation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"com.philips.microservices.core.observation", "com.philips.microservices.util"})
public class ObservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservationServiceApplication.class, args);
    }
}
