---
title: Introduction
layout: default
---

{% include toc.html %}

## Microservices
Microservices have been introduced many times before al over the web. If you need a good introduction please read [Martin Fowler](http://martinfowler.com/microservices/)'s introduction. This series continues assuming you have basic understanding of microservices.

The remainder of this article will cover three important aspects to rollout your microservice architecture.

1. Architecture
2. Continues Delivery
3. Organization

## Architecture
To be able to manage and maintain a microservice landscape at an enterprise scale we must define an architecture to partition our microservices. Many options are available here, and we by no means want to say the one presented here is the only right one. Your situation can require a different architecture, as long as you establish one before you start to prevent ending up with a bowl of spaghetti.

The architecture for this series discerns three layers of services:

* Core Services
* Composite Services
* API Services

![](../images/introduction-architecture.png)
