---
title: Index
layout: default
---

{% include toc.html %}

Welcome to the blog series demonstrating the HSDP Application Development Environment (ADE). This series is meant as introduction to the HSDP ADE as well as layout the develop experience. The series covers deploying microservices in local development environment, and the cloud using applications platforms and infrastructures for Docker.


## **Introduction** - An operation model for Microservices
Describe the architectural idea of how to partition our microservices developed in alpha, beta, gamma, and delta.

[Read full article](articles/introduction)

## **Alpha** - Base services + Edge server (Zuul), Load Balancer (Ribbon), Discovery (Eureka)
Create and test the first microservices patient-composite (composite), patient (core), observation (core), episode (core), edge-server (support), discovery (support).

[Read full article](articles/alpha)

## **Beta** - Circuit Breaker, Monitor Dashboard (Hystrix + Turbine)
Add a circuit breaker and monitor dashboard (Hystrix + Turbine) to the example built in Alpha and verify that short circuits are opened when services go offline.

## **Gamma** - OAuth 2.0
Add a new service (product-api), that will act as the external API (a Resource Server in OAuth terminology). Expose its services through the edge server introduced in Alpha acting as a token relay. Add OAuth Authorization Server and OAuth client.

## **Delta** - Dockerize!
Dockerize everything!

## **Epsilon** - An AngularJS client
Add a client with actual UI!

## **Zeta** - A/B Testing
Learn how we support A/B testing with our current environment.

## **Èta** - Cloud Foundry and Warden containers via Cloud Rocker
Philips is using Cloud Foundry which has its own container system. Docker on CF might not be the best choice. In this article we explore the possibilities running Warden containers.

## **Thèta** - The future with Otto
Otto promises to be the one tool to handle all the aspects of development and deployment to any cloud platform. This article discusses the current status of Otto (beta) and provides some workable code to see for yourself.
