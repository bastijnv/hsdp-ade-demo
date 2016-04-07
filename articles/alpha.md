---
title: Alpha - Base services + Edge server (Zuul), Load Balancer (Ribbon), Discovery (Eureka)
layout: default-article
---

{% include toc.html %}

## Introduction
In the [introduction](introduction.html) we presented an operations model that will be gradually
realized in this and subsequent articles. This article realizes the base services, made up by one
composite service and three core services, the service discovery service (Eureka), the edge server
(Zuul), and the load balancer (Ribbon). Throughout the article series we will use a number items
from the [Spring Cloud](http://projects.spring.io/spring-cloud/) family. To a large extent Spring
Cloud is based on components from [Netflix OSS](http://netflix.github.io/).

## Alpha Overview
To be able to develop and test our microservices we need an overview of the system landscape we want
to build. Below is the landscape that we will develop as part of Alpha. In subsequent articles we
will extent this landscape to finally match the landscape presented in
[introduction](introduction.html).

![](../images/alpha-overview.png)

The composite service, *Patient-composite*, aggregates data of the three core services: *Patient*,
*Observation*, and *Episode*. To support these services a *Service Discovery* service is provided
(Eureka). In addition there is the service that provides *Dynamic Routing* and *Load Balancing*
(Ribbon). The *Edge Server* (Zuul) is put in place as well. Note that the security aspect is not
yet part of this article. In other words, you will be able to directly target the core services
bypassing the edge server (see below).

## Source
To get the source used in the remainder of this article you can checkout the GIT repo.
  
```bash
$ git clone https://github.com/bastijnv/hsdp-ade-demo.git
$ cd hsdp-ade-demo
$ git checkout -b alpha
```

In true microservice spirit, each component is built separately and thus has its own build file.
[Gradle](http://gradle.org/) is used as the build system. For convenience a script is available to
build all the microservices in one command.

```bash
$ ./build-all.sh
```

> *Note:* Windows users should use the build-all.bat file.

Running the command should result in six times `BUILD SUCCESSFUL`, for each of the microservices
introduced above.

## Source Code Overview
Now that we have our source code checked out and built, let's walk through the code architecture and
highlight the key elements. Each microservice is a standalone
[Spring Boot](http://projects.spring.io/spring-boot/) application using [Undertow](http://undertow.io/)
as its web server.
[Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) is
used to implement the REST based services.

### Gradle
Spring Cloud has defined a set of starter dependencies to kickstart development. To use Eureka and
Ribbon in a microservice we only have to add the following dependency to our gradle build file:

```
compile("org.springframework.cloud:spring-cloud-starter-eureka:1.0.0.RELEASE")
```

A complete gradle file can be found
[here](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/core/patient-service/build.gradle).
In the same way we can add the dependency for an Eureka server to our Discovery Server microservice 
([see also](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/support/discovery-server/build.gradle)):

```
compile('org.springframework.cloud:spring-cloud-starter-eureka-server:1.0.0.RELEASE')
```

### Support microservices
In Alpha we use two supporting microservices: edge-server (Eureka) and discovery-server (Zuul).
Spring helps us to setup these services quickly. For example, to setup an Eureka server we can add
`@EnableEurekaServer` to a standard Spring application.

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
```

The complete code is not much more than shown above, as you can see in
[EurekaApplication.java](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/support/discovery-server/src/main/java/com/philips/microservices/support/discovery/EurekaApplication.java).
Zuul is setup in a similar manner using `@EnableZuulProxy` as can be seen in
[ZuulApplication.java](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/support/edge-server/src/main/java/com/philips/microservices/support/edge/ZuulApplication.java).

The above code sets up a default instance of Eureka and Zuul. The default setting can be easily
overridden to fit your project requirements. For instance, in our case we want Zuul to only allow
routing calls to our patient-composite service and block any requests to the core services. This
can be done by placing the following code in the
[application.yml](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/support/edge-server/src/main/resources/application.yml).

```yml
zuul:
  ignoredServices: "*"
  routes:
    patientcomposite:
      path: /patientcomposite/**
```

### Microservices
Alpha has four microservices, three of them are referred to as the core services (patient,
observation, episode) and one composite service (patient-composite). The important parts of these
services are how they handle subscribing themselves with the discovery server, and the dynamic
routing (load balancing).

To let a microservice automatically register itself with the Eureka discovery server we only have to
add `@EnableDiscoveryClient` to the Spring boot application as can be seen in
[PatientCompositeServiceApplication.java](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/composite/patient-composite-service/src/main/java/com/philips/microservices/composite/patient/PatientCompositeServiceApplication.java)
for example.

```java
@SpringBootApplication
@EnableDiscoveryClient
public class PatientCompositeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientCompositeServiceApplication.class, args);
    }
}
```

As mentioned in the introduction already, Ribbon is used to help us with the dynamic routing aspect.
To lookup and call an instance of a microservice using a Spring RestTemplate we only need to know the
name of the service. Ribbon, our load balancer, will find the service instance and return its URL
to the service consumer. See
[Util.java](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/composite/patient-composite-service/src/main/java/com/philips/microservices/composite/patient/service/Util.java)
and
[PatientCompositeIntegration.java](https://github.com/bastijnv/hsdp-ade-demo/blob/alpha/microservices/composite/patient-composite-service/src/main/java/com/philips/microservices/composite/patient/service/PatientCompositeIntegration.java).

```java
@Autowired
    private LoadBalancerClient loadBalancer;
    ...
    public ResponseEntity<List<Observation>> getObservations(int patientId) {

            ServiceInstance instance = loadBalancer.choose("observation");
            URI uri = instance.getUri();
		    ...
            response = restTemplate.getForEntity(url, String.class);

```

## Testing the system
Soon to come.