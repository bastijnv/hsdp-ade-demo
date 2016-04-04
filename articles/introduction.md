---
title: Introduction
layout: default
---

{% include toc.html %}

## Microservices
Microservices have been introduced many times before al over the web. If you need a good introduction please read [Martin Fowler](http://martinfowler.com/microservices/)'s 
introduction. This series continues assuming you have basic understanding of microservices.

The remainder of this article will cover three important aspects to rollout your microservice architecture.

1. Architecture
2. Continues Delivery
3. Organization

## Architecture
To be able to manage and maintain a microservice landscape at an enterprise scale we must define an architecture to partition our microservices. Many options 
are available here, and we by no means want to say the one presented here is the only right one. Your situation can require a different architecture, as long 
as you establish one before you start to prevent ending up with a bowl of spaghetti.

The architecture for this series discerns three layers of services:

* Core Services
* Composite Services
* API Services

Horizontally you could separate services by domain.

![](images/introduction-architecture.png)

## Challenges

The above introduced architecture introduces a number of challenges which need to be addressed:

* **Configuration of the microservices** 
With multiple (many) microservice instances deployed across multiple servers handling configuration of those services becomes 
a considerable architectural task. How to manage all the configuration files, and how to update them at runtime.  
* **Deployment of (multiple) microservices**
We have to keep track of what microservices are deployed, and where. Which hosts and ports are exposed. Especially important 
because of the continuous changes in the system landscape
using cloud deployments.
* **Failure handling**
How to prevent a chain of failure with interconnected microservices that depend on each other. How to handle timeouts of 
microservice requests or what happens when a microservice crashes.
* **Service(s) Health monitoring**
Related to failure handling. How can we provide an overview of the health of our microservices. Which services are running, 
which are down, etc.
* **Security** 
Same as for a monolith, but this time each microservice needs to be secured.
* **Service exposure**
Part of security is managing only the right microservices are exposed to the public.
* **Inter-service communication**
For root cause analysis it should be possible to find out which microservice is the root of the problem. On a combined request, how 
can we find out which microservice is causing issues in the chain of services? 
* **Routing**
With automatic load balancing spinning up or removing instances constantly there is no time to manually update routing tables. 

## Operation Model

This section introduces an operations model that addresses the challenges mentioned in the previous section. An overview is presented 
in the figure below.

The *Central Configuration Server* addresses the first challenge of centralizing configuration management. We 
use [Eureka](https://www.google.com "Eureka on Github") as our *Discovery Server*. Microservices can self-register at startup 
at this discovery server so we do not have to keep manually about which server is (un)available.
 
 
![](images/introduction-operations-model.png)