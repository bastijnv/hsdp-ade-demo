package com.philips.microservices.core.observation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ObservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservationServiceApplication.class, args);
    }
}
