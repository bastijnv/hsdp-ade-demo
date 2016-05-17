---
title: Gamma - OAuth 2.0
layout: default-article
---

{% include toc.html %}

## Introduction
Security is an important topic for any application. In a microservice architecture it requires some additional work as each 
microservice has to verify the authentication and authorization of the current user. Luckily, our Edge server simplifies this.

For an introduction to OAuth2 read the article by [Digital Ocean](https://www.digitalocean.com/community/tutorials/an-introduction-to-oauth-2). 
Readers who are really interested can also browse the [specification](https://tools.ietf.org/html/rfc6749). As mentioned before, in this
series security is handled in two steps. First of all, to prevent unauthorized access to the internal microservices we use [Zuul](https://github.com/Netflix/zuul) as our *Edge Server* that all external traffic goes through. The remaining exposed API services are protected by applying *OAuth2* via 
[Spring Cloud](http://projects.spring.io/spring-cloud/) and [Spring Cloud Security OAuth2](http://projects.spring.io/spring-security-oauth/). 

![](../images/gamma-oauth-abstract-flow.png)

## Gamma Overview
To be able to develop and test our microservices we need an overview of the system landscape we want to build. Below is the landscape that we will develop as part of Gamma. 
In subsequent articles we will extent this landscape to finally match the landscape presented in [introduction](introduction.html).

![](../images/gamma-overview.png)

The four roles in OAuth are: (1) Resource Owner, (2) Client, (3) Resource Server, (4) Authorization Server. In our operations model 
shown in the overview above we introduce an *OAuth Authorization Server* as a separate component. The API services will act as *OAuth Resource Server* and the
external API consumers will be the *OAuth Resource Clients*. Finally, the edge server will act as *OAuth Token Relay* meaning it is an
*OAuth Resource Server* that passes through the *OAuth Access Tokens* that are contained in the external requests to the API services.
Note that the role of Authorization Server is often
taken by either public providers such as Facebook, Google, or LinkedIN, or by of-the-shelve packages as ForgeRock. Later in this article series
we will address this in more detail. In the remainder of this article we will test all four authorization grant flows to get an access token from the Authorization server. 

> Note that when we talk security doing everything over HTTP is not advised. In any real world scenario you should
> route all your traffic over HTTPS. For the sake of keeping things simple in this article we will continue to 
> use HTTP.

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
Implementation of a default Authorization Server is trivial with Spring Boot. We only have to add the `@EnableAuthorizationServer` annotation and 
use a configuration class to register the clients, scopes, and allowed grant flows. Example code can be found at [AuthserverApplication.java](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/support/auth-server/src/main/java/demo/AuthserverApplication.java). This setup registers our clients in-memory only. 

> **NOTE** In a production system the user account data would be managed in a backend database (most likely a directory service), 
> not hard coded in the Spring Configuration. Sample applications connecting to such a database are easy to find on the internet, 
> for example in the Spring Security Samples.

```java
@Configuration
@EnableAuthorizationServer
protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("acme")
			.secret("acmesecret")
			.authorizedGrantTypes("authorization_code", "refresh_token", "implicit", "password", "client_credentials")
			.scopes("pophealth");
	}
}
```

New users can be added in [application.properties](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/support/auth-server/src/main/resources/application.properties), adding a new entry for the new user like `security.user.password=password`. The end-user pages for authentication
and user consent can be found at the [templates](https://github.com/bastijnv/hsdp-ade-demo/tree/gamma/microservices/support/auth-server/src/main/resources/templates) directory.

Spring automatically forwards the OAuth access tokens from the Edge Server to the API Service by default, so no code is required to make this work. We do,
however, modify the reverse proxy settings in [edge-server/application.yml](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/support/edge-server/src/main/resources/application.yml) to get shorter URLs.

```java
zuul:
  ignoredServices: "*"
  prefix: /api
  routes:
    patientapi: /patient/**
```

This gives URLs like <http://localhost:8765/api/patient/123> instead of <http://localhost:8765/patientapi/patient/123> as we 
used in the previous articles. The route to the composite-service is also replaced with a route to the api-service.

### Microservices
The *patient-api-service* must be configured to be an OAuth Resource Server. As you might guess, again Spring helps us out by providing a
`@EnableOAuth2Resource` annotation. Annotating [PatientApiServiceApplication.java](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/api/patient-api-service/src/main/java/com/philips/microservices/api/patient/PatientApiServiceApplication.java) with this annotation is sufficient to configure the 
service to be an OAuth Resource Server.

```java
@EnableOAuth2Resource
public class PatientApiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientApiServiceApplication.class, args);
    }
}
```

The implementation of the API service can be found in [PatientApiService.java](https://github.com/bastijnv/hsdp-ade-demo/blob/gamma/microservices/api/patient-api-service/src/main/java/com/philips/microservices/api/patient/service/PatientApiService.java) and is 
similar to the patient-composite service.

```java
@RequestMapping("/{patientId}")
@HystrixCommand(fallbackMethod = "defaultPatientComposite")
public ResponseEntity<String> getPatientComposite(
    @PathVariable int patientId,
    @RequestHeader(value="Authorization") String authorizationHeader,
    Principal currentUser) {

    LOG.info("PatientApi: User={}, Auth={}, called with patientId={}", currentUser.getName(), authorizationHeader, patientId);
    URI uri = loadBalancer.choose("patientcomposite").getUri();
    String url = uri.toString() + "/patient/" + patientId;
    LOG.debug("GetPatientComposite from URL: {}", url);

    ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
    LOG.info("GetPatientComposite http-status: {}", result.getStatusCode());
    LOG.debug("GetPatientComposite body: {}", result.getBody());

    return result;
}
```

We have removed the `/patient` from the RequestMapping to be in line with the changes to the Edge Server mentioned before.

## Testing the system
Ensure your RabbitMQ is running as explained in the Beta article, then start the services.

```bash
$ cd support/auth-server;       ./gradlew bootRun
$ cd support/discovery-server;  ./gradlew bootRun
$ cd support/edge-server;       ./gradlew bootRun
$ cd support/monitor-dashboard; ./gradlew bootRun
$ cd support/turbine;           ./gradlew bootRun

$ cd core/patient-service;                 ./gradlew bootRun
$ cd core/observation-service;             ./gradlew bootRun
$ cd core/episode-service;                 ./gradlew bootRun
$ cd composite/patient-composite-service;  ./gradlew bootRun
$ cd api/patient-api-service;             ./gradlew bootRun
```

Wait till the `DiscoveryClient ... - registration status: 204` appears on your services or you see them registred in your
Eureka page then continue to test the different authorization flows.

### Authorization Code Grant
The authorization code grant type is the most commonly used because it is optimized for server-side applications, where source code is not publicly exposed, and Client Secret confidentiality can be maintained. This is a redirection-based flow, which means that the application must be capable of interacting with the user-agent (i.e. the user's web browser) and receiving API authorization codes that are routed through the user-agent.

To initiate an authorization code token grant you visit the authorization endpoint, e.g. at <http://localhost:9999/uaa/oauth/authorize?response_type=code&client_id=acme&redirect_uri=http://example.com&scope=pophealth&state=97536> once you have 
authenticated (user/password) and accept the consent you will get a redirect to example.com with an authorization code attached, 
e.g. <http://example.com/?code=VuBeo9&state=97536>.

<img src="../images/gamma-consent.png" width="425px" height="195px" style="display:block;margin:auto;">

> **NOTE** The state parameter should be set to a random value in the request and checked on the response for preventing cross-site request forgery.

> **NOTE** For the purposes of this sample application we have created a client "acme" with no registered redirect, which is what enables us to get 
> a redirect to example.com. In a production application you should always register a redirect (and use HTTPS).

The code can be exchanged for an access token using the "acme" client credentials on the token endpoint. 

```cURL
curl acme:acmesecret@localhost:9999/uaa/oauth/token \
  -d grant_type=authorization_code \
  -d client_id=acme \
  -d redirect_uri=http://example.com \
  -d code=VuBeo9 -s | jq .
{
  "access_token": "7f986847-7363-4c6a-b122-d15e0d18d519",
  "token_type": "bearer",
  "refresh_token": "1a77508c-9fde-4641-9d3f-d97b44376519",
  "expires_in": 43199,
  "scope": "pophealth"
}
```

The access token is a UUID ("7f986847-7363-4c6a-b122-d15e0d18d519"), backed by an in-memory token store in the server. We also got a refresh token that we can use to get a new access token when the current one expires. Save the access token in an environment variable for later.

```bash
export TOKEN=7f986847-7363-4c6a-b122-d15e0d18d519
```

Trying to exchange the same code again should result in an error as the CODE functions as a One Time Pin (OTP).

```cURL
curl acme:acmesecret@localhost:9999/uaa/oauth/token \
 -d grant_type=authorization_code \
 -d client_id=acme \
 -d redirect_uri=http://example.com \
 -d code=VuBeo9 -s | jq .
{
  "error": "invalid_grant",
  "error_description": "Invalid authorization code: VuBeo9"
}
```

Or with Postman using its OAuth2 support.

> **NOTE** Currently Authorization Code grant is not working in Postman. We are not sure why.

![postman-auth-code](../images/gamma-postman-auth-code.png)

### Implicit Grant
The implicit grant type is used for mobile apps and web applications (i.e. applications that run in a web browser), where the client secret confidentiality is not guaranteed. The implicit grant type is also a redirection-based flow but the access token is given to the user-agent to forward to the application, so it may be exposed to the user and other applications on the user's device. Also, this flow does not authenticate the identity of the application, and relies on the redirect URI (that was registered with the service) to serve this purpose. The implicit grant type does not support refresh tokens.

To initiate the implicit grant browse to <http://localhost:9999/uaa/oauth/authorize?response_type=token&client_id=acme&redirect_uri=http://example.com& scope=pophealth&state=48532>, login with (user / password) and give your consent. After doing so you will be redirected to an URL like
<http://example.com/#access_token=7f986847-7363-4c6a-b122-d15e0d18d519&token_type=bearer&state=48532&expires_in=42253&scope=pophealth>. This
URL contains the access token which you can again save to an evironment variable for later use.

```bash
export TOKEN=7f986847-7363-4c6a-b122-d15e0d18d519
```

> **NOTE** If you browsed without incognito mode enabled during the Authorization Code test you will still be logged in from before 
> and will be redirected directly to the redirect uri. 

### Resource Owner Password Credentials Grant
With the resource owner password credentials grant type, the user provides their service credentials (username and password) directly to the application, which uses the credentials to obtain an access token from the service. This grant type should only be enabled on the authorization server if other flows are not viable. Also, it should only be used if the application is trusted by the user (e.g. it is owned by the service, or the user's desktop OS).

> **NOTE** We are not sure why, but firing the request from Postman fails while the exported cURL works. 
> Nonetheless, we added it for you.

```cURL
postman (5)

curl -s acme:acmesecret@localhost:9999/uaa/oauth/token  \
  -d grant_type=password \
  -d client_id=acme \
  -d scope=pophealth \
  -d username=user \
  -d password=password | jq .
{
  "access_token": "7f986847-7363-4c6a-b122-d15e0d18d519",
  "token_type": "bearer",
  "refresh_token": "1a77508c-9fde-4641-9d3f-d97b44376519",
  "expires_in": 41716,
  "scope": "pophealth"
}
```

### Client Credentials Grant
The client credentials grant type provides an application a way to access its own service account. Examples of when this might be useful include if an application wants to update its registered description or redirect URI, or access other data stored in its service account via the API.

```cURL
curl -s acme:acmesecret@localhost:9999/uaa/oauth/token  \
  -d grant_type=client_credentials \
  -d scope=pophealth | jq .
{
  "access_token": "b835dd2e-8b1d-4d3b-977c-55dffab24daf",
  "token_type": "bearer",
  "expires_in": 43199,
  "scope": "pophealth"
}
```

Note that the access token in this case represent the client application, not the resource owner (i.e. the user). 
You can save to an evironment variable for later use.

```bash
export TOKEN=b835dd2e-8b1d-4d3b-977c-55dffab24daf
```

You can also run this from Postman using its OAuth2 support.

![postman-client-creds](../images/gamma-postman-client-creds.png)

### Using the access token
Now that we have an access token let's use it to communicate with our API service. First verify that access
without an access token, or with an invalid access token fails.

```cURL
postman (6)

curl 'http://localhost:8765/api/patient/123' -s | jq .
{
  "error": "unauthorized",
  "error_description": "Full authentication is required to access this resource"
}
```

```cURL
postman (7)

curl 'http://localhost:8765/api/patient/123' \
  -H  "Authorization: Bearer invalid-access-token" -s | jq .
{
  "error": "access_denied",
  "error_description": "Unable to obtain a new access token for resource 'null'. The provider manager is not configured to support it."
}
```

Now that we have verified that throws access denied, let's add our access token to send a correct request. Note that we
have stored a valid access token in the `TOKEN` environment variable.

```cURL
postman (8) | replace your token

curl 'http://localhost:8765/api/patient/123' \
  -H  "Authorization: Bearer $TOKEN" -s | jq .
{
  "patientId": 123,
  "name": "name",
  "birthDate": "01-01-2000",
  "observations": [
    {
      "observationId": 1,
      "type": "Steps",
      "value": 100
    },
    {
      "observationId": 2,
      "type": "HearthRate",
      "value": 63
    },
    {
      "observationId": 3,
      "type": "Steps",
      "value": 400
    }
  ],
  "episodes": [
    {
      "episodeId": 1,
      "referral": "Acme",
      "tac": "eCAC"
    },
    {
      "episodeId": 2,
      "referral": "Acme",
      "tac": "eTrAC"
    },
    {
      "episodeId": 3,
      "referral": "Acme",
      "tac": "eTrAC"
    }
  ]
}
```

If we have a look at the logs of the api-service we see that the API service contacts the Authorization Server to get the user info and prints
the acquired info in the logs.

```
2016-05-16 09:32:14.799  INFO 62825 --- [entApiService-4] c.p.m.a.p.service.PatientApiService      : GetPatientComposite http-status: 200
2016-05-16 09:32:15.135  INFO 62825 --- [  XNIO-2 task-8] o.s.c.s.o.r.UserInfoTokenServices        : Getting user info from: http://localhost:9999/uaa/user
2016-05-16 09:32:15.235  INFO 62825 --- [entApiService-5] c.p.m.a.p.service.PatientApiService      : PatientApi: User=user, Auth=Bearer 3dd89559-39ef-4221-ab64-ffbb00d48e1c, called with patientId=123
```

## Wrap-up
We have secured our resources with OAuth2. We have tested the four grant protocols and verified that the token obtained from them 
can be used to get data from our protected resources. We also verified that invalid tokens properly got an access denied as response.

You must be tired starting all services manually each time. In the [next](delta.html) article we introduce Docker to make
this much more convenient for you! 





