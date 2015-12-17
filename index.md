---
title: Index
layout: default
---

Welcome to the blog series demonstrating the HSDP Application Development Environment (ADE). This series is meant as introduction to the HSDP ADE as well as layout the develop experience. The series covers deploying microservices in local development environment, and the cloud using applications platforms and infrastructures for Docker.


## Introduction - An operation model for Microservices
Describe the architectural idea of how to partition our microservices developed in alpha, beta, gamma, and delta.

## Alpha - Base services + Edge server (Zuul), Load Balancer (Ribbon), Discovery (Eureka)
Create and test the first microservices patient-composite (composite), patient (core), observation (core), episode (core), edge-server (support), discovery (support).

## Beta - Circuit Breaker, Monitor Dashboard (Hystrix + Turbine)
Add a circuit breaker and monitor dashboard (Hystrix + Turbine) to the example built in Alpha and verify that short circuits are opened when services go offline.

## Gamma - OAuth 2.0
Add a new service (product-api), that will act as the external API (a Resource Server in OAuth terminology). Expose its services through the edge server introduced in Alpha acting as a token relay. Add OAuth Authorization Server and OAuth client.

## Delta - Dockerize!
Dockerize everything!
