---
title: Beta - Circuit Breaker, Monitor Dashboard (Hystrix + Turbine)
layout: default-article
---

{% include toc.html %}

## Introduction
In a microservice landscape there is a big risk a certain day and time a microservice might no longer respond, 
or its responses are so delayed the system is no longer functional. This non-behaving microservice might in turn
cause other microservices that depend on it fail. This fenomenom is called *chain of failure*, where errors in
one component can cause other components to fail that depend on it. With a potential large number of microservices
you might understand this needs to be monitored and handled constantly.

One way to handle this is discussed in the remainder of this article. There are multiple ways to do this but in this series we will be using a *circuit breaker* ([Hystrix](https://github.com/Netflix/Hystrix)). For an introduction on circuit breakers read [Fowler's post](http://martinfowler.com/bliki/CircuitBreaker.html). A typical circuit breaker applies state transitions like seen in the image below. When a service is behaving as expected the circuit will be closed and business is as usual. Requests are made to the target service. When a service starts failing the circuit breaker waits for a threshold to be reached after which it opens the circuit. An open circuit will prevent the request to the target service is not made but a cached response is returned immediately. After a set period the circuit breaker will test if the circuit can be closed again by allowing some request to pass to the target request. When the target service is available again the circuit will be closed again.

![](../images/beta-circuit-breaker.png)

[Turbine](https://github.com/Netflix/Turbine) can provide the *Hystrix Dashboard* from information on all circuit breakers in the system, which it can obtain from Eureka.

## Beta Overview
To be able to develop and test our microservices we need an overview of the system landscape we want to build. Below is the landscape that we will develop as part of Beta. Highlighted in red are the added *circuit breaker* and *monitor dashboard*.
In subsequent articles we will extent this landscape to finally match the landscape presented in [introduction](introduction.html).

![](../images/beta-overview.png)



## Source
To get the source used in the remainder of this article you can checkout the GIT repo.
  
```bash
$ git clone https://github.com/bastijnv/hsdp-ade-demo.git
$ cd hsdp-ade-demo
$ git checkout -b beta
```
