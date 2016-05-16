---
title: Index
layout: default
---

{% include toc.html %}

Welcome to the blog series demonstrating the HSDP Application Development Environment (ADE). This series is meant as introduction to the HSDP ADE as well as layout the develop experience. The series covers deploying microservices in local development environment, and the cloud using applications platforms and infrastructures for Docker.


## **Introduction** - An operation model for Microservices
Describe the architectural idea of how to partition our microservices developed in alpha, beta, gamma, and delta.

[Read full article](articles/introduction.html)

## **Alpha** - Base services + Edge server (Zuul), Load Balancer (Ribbon), Discovery (Eureka)
Create and test the first microservices patient-composite (composite), patient (core), observation (core), episode (core), edge-server (support), discovery (support).

[Read full article](articles/alpha.html)

## **Beta** - Circuit Breaker, Monitor Dashboard (Hystrix + Turbine)
Add a circuit breaker and monitor dashboard (Hystrix + Turbine) to the example built in Alpha and verify that short circuits are opened when services go offline.

[Read full article](articles/beta.html)

## **Gamma** - OAuth 2.0
Add a new service (patient-api), that will act as the external API (a Resource Server in OAuth terminology). Expose its services through the edge server introduced in Alpha acting as a token relay. Add OAuth Authorization Server and OAuth client.

[Read full article](articles/gamma.html)

## **Delta** - Dockerize!
Docker allows you to package an application with all of its dependencies into a standardized unit for software development. It also adds a lot
of convenience so we do no longer have to manually start all services from the command line one by one!

[Read full article](articles/delta.html)

## **Epsilon** - An AngularJS client
Add a client with actual UI!

[Read full article](articles/epsilon.html)

## **Zeta** - HSDP Access Management
Learn how to replace the Authentication server from article Gamma with the Philips HealthSuite Digital Platform Authentication server.

## **Èta** - HSDP Identity Management
Learn how to use Philips HealthSuite Digital Platform Identity Management services.

## **Thèta** - A/B Testing
Learn how we support A/B testing with our current environment.
