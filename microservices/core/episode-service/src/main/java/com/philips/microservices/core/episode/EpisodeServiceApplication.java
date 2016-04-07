package com.philips.microservices.core.episode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EpisodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpisodeServiceApplication.class, args);
    }
}
