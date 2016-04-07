package com.philips.microservices.composite.patient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
public class PatientCompositeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientCompositeServiceApplication.class, args);
    }
}
