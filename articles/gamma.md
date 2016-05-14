---
title: Gamma - OAuth 2.0
layout: default-article
---

{% include toc.html %}

## Introduction
Security is an important topic for any application. In a microservice architecture it requires some additional work as each 
microservice has to verify the authentication and authorization of the current user. Luckily, our Edge server simplifies this.

For an introduction to OAuth2 read the article by [Digital Ocean](https://www.digitalocean.com/community/tutorials/an-introduction-to-oauth-2). 
Readers who are really interested can also browse the [specification](https://tools.ietf.org/html/rfc6749).

## Gamma Overview
To be able to develop and test our microservices we need an overview of the system landscape we want to build. Below is the landscape that we will develop as part of Gamma. 
In subsequent articles we will extent this landscape to finally match the landscape presented in [introduction](introduction.html).

![](../images/gamma-overview.png)

In this article we will add a new microservice, the *patient-api-service*, that will act as the *resource server* in OAuth2 terminology.
Its API will be exposed through the Edge server, which acts as *token relay*, forwarding OAuth2 access tokens from the client to the 
resource server. An OAuth2 *Authorization Server* is also added to make this sample work. Note that the role of Authorization Server is often
taken by either public providers such as Facebook, Google, or LinkedIN, or by of-the-shelve packages as ForgeRock. Later in this article series
we will address this in more detail. In the remainder of this article we will test all four authorization grant flows to get an access token from the Authorization server. 

> Note that when we talk security doing everything over HTTP is not advised. In any real world scenario you should
> route all your traffic over HTTPS. For the sake of keeping things simple in this series we will continue to 
> use HTTP exclusively.

## Source
To get and build the source used in the remainder of this article you can checkout the GIT repo.
  
```bash
$ git clone https://github.com/bastijnv/hsdp-ade-demo.git
$ cd hsdp-ade-demo
$ git checkout -b gamma
$ ./build-all.sh
```

## Source Code Overview

### Gradle
Again Spring Cloud is going to help us out by providing two open source projects [spring-cloud-security](http://cloud.spring.io/spring-cloud-security/) 
and [spring-security-oauth2](http://projects.spring.io/spring-security-oauth/). To implement the auth server we add the following
dependencies to [auth-server/build.gradle](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/support/auth-server/build.gradle).

```bash
compile("org.springframework.boot:spring-boot-starter-security")
compile("org.springframework.security.oauth:spring-security-oauth2:2.0.6.RELEASE"		
```
and for the [patient-api-service/build.gradle](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/api/patient-api-service/build.gradle).

```bash
compile("org.springframework.cloud:spring-cloud-starter-security:1.0.0.RELEASE")
compile("org.springframework.security.oauth:spring-security-oauth2:2.0.6.RELEASE")
```

### Support microservices

