---
title: Delta - Dockerize!
layout: default-article
---

{% include toc.html %}

## Introduction
Docker containers wrap up a piece of software in a complete filesystem that contains everything it needs to run: code, runtime, system tools, system libraries – anything you can install on a server. This guarantees that it will always run the same, regardless of the environment it is running in. 
They are based on open standards allowing containers to run on all major Linux distributions and Microsoft operating systems with support for every infrastructure.

Containers running on a single machine all share the same operating system kernel so they start instantly and make more efficient use of RAM. Images are constructed from layered filesystems so they can share common files, making disk usage and image downloads much more efficient. Containers isolate applications from each other and the underlying infrastructure while providing an added layer of protection for the application.

Compared to VMs, Docker containers have similar resource isolation and allocation benefits as virtual machines but a different architectural approach allows them to be much more portable and efficient.

![Docker](../images/delta-docker.png)

In addition, Docker adds a lot to the developer experience via [Docker Machine](https://docs.docker.com/machine/), [Docker Compose](https://docs.docker.com/compose/), and [Docker Swarm](https://docs.docker.com/swarm/). Docker Machine is a tool that lets you install Docker Engine on virtual hosts, and manage the hosts with docker-machine commands. You can use Machine to create Docker hosts on your local Mac or Windows box, on your company network, in your data center, or on cloud providers like AWS or Digital Ocean. Compose is a tool for defining and running multi-container Docker applications. Docker Swarm is native clustering for Docker. It turns a pool of Docker hosts into a single, virtual Docker host.

Please see the Docker [website](https://www.docker.com/) for more information.

## Delta Overview
Soon to come.

## Docker Installation
Please install and setup Docker for your environment following the [Get Started](https://docs.docker.com/mac/started/) for Mac or [Get Started](https://docs.docker.com/windows/started) for windows. Return once you have Docker installed.

> **NOTE** Recently Docker Beta was launched. Since you have to subscribe we do not use it for this article series. However, feel 
> free to subscribe and use docker beta. Faster, and more reliable without VirtualBox! See the 
> [announcement](https://blog.docker.com/2016/03/docker-for-mac-windows-beta/) for more information.

If you are on Windows or Mac, Docker by default creates a VM for you named *default*. For this series we will create our own VM with slightly changed 
specifications. Create a virtual machine named *dev* with 4096MB memory and 32GB disk size with the command below. After that start the new machine.
and configure the docker client. The configuration command on windows can be run without the `eval`. To test if everything succeeded run `docker ps`, it should
return as shown below.

```bash
$ docker-machine create -d virtualbox --virtualbox-memory "4096" --virtualbox-disk-size "32000" dev
$ docker-machine start dev
$ eval $(docker-machine env dev)
```


```
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```

For convenience we map the IP of our dev box to docker in our hosts file. Lookup the IP and add to your hosts file.

```bash
$ docker-machine ip dev
192.168.99.100
```

```
##
# Host Database
#
# localhost is used to configure the loopback interface
# when the system is booting.  Do not change this entry.
##
127.0.0.1	localhost
255.255.255.255	broadcasthost
::1             localhost
192.168.99.100  docker
```

Great, you are setup to work with Docker! 

## Source
To get the source used in the remainder of this article you can checkout the GIT repo.
  
```bash
$ git clone https://github.com/bastijnv/hsdp-ade-demo.git
$ cd hsdp-ade-demo
$ git checkout -b delta
```

Gradle is configured to use the [gradle-docker](https://github.com/Transmode/gradle-docker) plugin to build and publish 
Docker images from the build script. During the build you will see this in the logs.

> **NOTE** Before you run the build-all script be sure to have successfully completed the steps in the Docker Installation section.

```bash
:distDocker
Sending build context to Docker daemon 35.63 MB
```

When the build completes successfully we can print the Docker images to verify everything went well.

```bash
$ docker images | grep philips
philips/turbine                     latest                 9928b3b274f7        31 seconds ago       686 MB
philips/monitor-dashboard           latest                 d634acfea330        56 seconds ago       684.2 MB
philips/edge-server                 latest                 9f5ed6a177c5        About a minute ago   680 MB
philips/discovery-server            latest                 4b4837406d2f        About a minute ago   685.8 MB
philips/auth-server                 latest                 9729a38039f9        2 minutes ago        657.3 MB
philips/patient-api-service         latest                 04825c9996bb        2 minutes ago        693.2 MB
philips/patient-composite-service   latest                 ae848d3ad5f1        3 minutes ago        692 MB
philips/episode-service             latest                 666c1fbb54fc        4 minutes ago        677.5 MB
philips/observation-service         latest                 6863c77eb886        5 minutes ago        677.5 MB
philips/patient-service             latest                 b44f758eec74        6 minutes ago        677.5 MB
```

## Source Code Overview

### Gradle
As mentioned before Gradle is configured to build the docker images. In addition, to keep all Docker logic separate
from the rest we have added a separate [Spring Profile](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html) 
to our [application.yml](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/microservices/api/patient-api-service/src/main/resources/application.yml) files.

```java
# For deployment in Docker containers
spring:
  profiles: docker
  oauth2:
    resource:
      userInfoUri: https://auth:9999/uaa/user

server:
  port: 8080

app:
  rabbitmq:
    host: rabbitmq

eureka:
  instance:
    preferIpAddress: true
  client:
    serviceUrl:
      defaultZone: http://discovery:8761/eureka/
```

A few observations can be made: (1) we no longer need to worry about port collisions since each Docker container gets 
its own server and thus IP address. We can now use port 8080 without fear of port collisions. (2) To register our
microservices we need to do so by IP as else they would all get the same hostname. (3) We have to specify the
URL to Eureka, also running in another Container with name `discovery`.

### Support Microservices
Docker runs containers in a closed network. For security we will expose as few services from the closed network as possible. 
We need to expose the Edge service and OAuth Authorization server only. We will up the security a bit for the exposed services and use 
HTTPS with a self-signed certificate.

> **NOTE** Don't use the self-signed certificate for production code as the private part is hardcoded in the source code.

The certificate presented by the 
[Edge server](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/microservices/support/edge-server/src/main/resources/server.jks)
and [Authorization server](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/microservices/support/auth-server/src/main/resources/server.jks) can be found in their respective resources folders. Their [application.yml](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/microservices/support/edge-server/src/main/resources/application.yml) holds the SSL configuration.

```
server:
  port: 8765
  ssl:
    key-store: classpath:server.jks
    key-store-password: password
    key-password: password
```

### Microservices
The API service will communicate with the OAuth Authorization server to validate the user. For this it needs to communicate over HTTPS,
validating the certificate that is presented by the OAuth Authorization server. It uses a [truststore](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/microservices/api/patient-api-service/src/main/resources/truststore.jks) to do so which is configured in its 
[application.yml](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/microservices/api/patient-api-service/src/main/resources/application.yml) 

```
server:
  port: 0
  ssl:
    enabled: false
    # Problem with trust-store properties?
    #
    # Instead use: java -Djavax.net.debug=ssl -Djavax.net.ssl.trustStore=src/main/resources/truststore.jks -Djavax.net.ssl.trustStorePassword=password -jar build/libs/*.jar
    #
    # trust-store: classpath:truststore.jks
    trust-store: src/main/resources/truststore.jks
    trust-store-password: password
```

### Docker 
To start all our microservices we use [Docker Compose](https://docs.docker.com/compose/). If we configure Docker Compose correctly we
can start all microservices with a single command `docker-compose up -d` from the root directory holding the `docker-compose.yml`.
You configure Docker Compose via this yml file, 
[docker-compose.yml](https://github.com/bastijnv/hsdp-ade-demo/blob/delta/docker-compose.yml). A snapshot is shown below. The complete
file lists all the containers to start, forwards ports, and links services that need to know about each other.

> **NOTE** Links update the /etc/hosts of the container with the linked services.

```
...
discovery:
  image: philips/discovery-server
  ports:
    - "8761:8761"

auth:
  image: philips/auth-server
  ports:
    - "9999:9999"

patient:
  image: philips/patient-service
  links:
    - discovery
...

api:
  image: philips/patient-api-service
  links:
    - auth
    - discovery
    - rabbitmq
...
```

## Testing the system
Start the services with a single command from the root directory. 

```bash
$ docker-compose up -d
Creating adedemo_auth_1
Creating adedemo_rabbitmq_1
Creating adedemo_discovery_1
Creating adedemo_patient_1
Creating adedemo_api_1
Creating adedemo_edge_1
Creating adedemo_composite_1
Creating adedemo_monitor_1
Creating adedemo_observation_1
Creating adedemo_episode_1
```

You can see that they were added running the following command.

```bash
$ docker-compose ps
        Name                       Command               State                                             Ports
-----------------------------------------------------------------------------------------------------------------------------------------------------------
adedemo_api_1           /patient-api-service/bin/p ...   Up      8080/tcp
adedemo_auth_1          /auth-server/bin/auth-server     Up      0.0.0.0:9999->9999/tcp
adedemo_composite_1     /patient-composite-service ...   Up      8080/tcp
adedemo_discovery_1     /discovery-server/bin/disc ...   Up      0.0.0.0:8761->8761/tcp
adedemo_edge_1          /edge-server/bin/edge-server     Up      0.0.0.0:443->8765/tcp
adedemo_episode_1       /episode-service/bin/episo ...   Up      8080/tcp
adedemo_monitor_1       /monitor-dashboard/bin/mon ...   Up      0.0.0.0:7979->7979/tcp
adedemo_observation_1   /observation-service/bin/o ...   Up      8080/tcp
adedemo_patient_1       /patient-service/bin/patie ...   Up      8080/tcp
adedemo_rabbitmq_1      /docker-entrypoint.sh rabb ...   Up      15671/tcp, 0.0.0.0:15672->15672/tcp, 25672/tcp, 4369/tcp, 5671/tcp, 0.0.0.0:5672->5672/tcp
```

To see what is happening you can have a look at the logs. Sit back and relax while you refresh <http://docker:8761> from time to time to see
if Eureka is already running. The first time might be a bit long as nothing is cached for docker to speed things up.

> **NOTE** If you are watching the logs during startup some errors might be thrown. Most of times this is just the Discovery service not
> yet being available which causes other services to report they can't reach it to subscribe themselves.

```bash
$ docker-compose logs
auth_1        | 2016-05-16 13:07:05.597  INFO 1 --- [           main] io.undertow.servlet                      : Initializing Spring embedded WebApplicationContext
auth_1        | 2016-05-16 13:07:05.603  INFO 1 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 103537 ms
monitor_1     | 2016-05-16 13:07:41.204  WARN 1 --- [           main] io.undertow.websockets.jsr               : UT026009: XNIO worker was not set on WebSocketDeploymentInfo, the default worker will be used
monitor_1     | 2016-05-16 13:07:47.187  WARN 1 --- [           main] io.undertow.websockets.jsr               : UT026010: Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used
discovery_1   | 2016-05-16 13:07:51.141  WARN 1 --- [           main] io.undertow.websockets.jsr               : UT026009: XNIO worker was not set on WebSocketDeploymentInfo, the default worker will be used
```

Wait for <http://docker:8761/> to show all services are registered.

![delta-eureka](../images/delta-eureka.png)

Great! Now let's run some queries. First we have to Authorize ourselves. Store the token in an environment variable again for later use.

> As in the Gamma article, the Postman request is failing. Feel free to change it and let me know if you get it to work!

```cURL
postman (9)

$ curl https://acme:acmesecret@docker:9999/uaa/oauth/token \
   -d grant_type=password -d client_id=acme \
   -d username=user -d password=password -ks | jq .
{
  "access_token": "1a4d7615-ac52-4eb2-8158-f5c17886746f",
  "token_type": "bearer",
  "refresh_token": "f06edb37-9ebb-4ca5-aee7-d174231cf20e",
  "expires_in": 43199,
}
```

```
export TOKEN=1a4d7615-ac52-4eb2-8158-f5c17886746f
```

Now use the token to request some data. If you are still keeping an eye on the docker-compose logs in a separate window
you should see the microservices log the request. Due to the bug reported in the earlier article it might take some tries
before it returns the data. Once it does however it will always work directly. 

> **NOTE** If you get tired of this issue feel free to fork and deliver the fix in a pull request! Seems the core services
> do not respond fast enough or Hystrix falsely opens the circuit.

```cURL
postman (10) | run postman(9) or manually add access_token

$ curl -H "Authorization: Bearer $TOKEN" \
  -ks 'https://docker/api/patient/1046' | jq .
{
  "patientId": 1046,
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

E voila! You are now running a Docker landscape successfully. Let's harnass some of its powers and upscale a service.

```bash
$ docker-compose scale episode=2
Creating and starting 2 ... done
``` 

Verify it was started while you wait for it to register itself with Eureka.

```bash
$ docker-compose ps episode
      Name                     Command               State    Ports
---------------------------------------------------------------------
adedemo_episode_1   /episode-service/bin/episo ...   Up      8080/tcp
adedemo_episode_2   /episode-service/bin/episo ...   Up      8080/tcp
```

![delta-episode-scaled](../images/delta-episode-scaled.png)

If you now request data fast enough the load balancer routes it to either of the episode services denoted by `episode_1` and `episode_2`.
We can again use Apache Benchmarking to run many requests.

> **NOTE** You have to be really fast to force the load balancer to do so.

```
$ ab -n 30 -c 5 -H 'Authorization: Bearer 1a4d7615-ac52-4eb2-8158-f5c17886746f' https://docker/api/patient/1

api_1         | 2016-05-16 13:31:03.024 DEBUG 1 --- [ntApiService-19] c.n.h.c.javanica.command.GenericCommand  : execute command: getPatientComposite
api_1         | 2016-05-16 13:31:03.027  INFO 1 --- [ntApiService-19] c.p.m.a.p.service.PatientApiService      : PatientApi: User=user, Auth=Bearer 1a4d7615-ac52-4eb2-8158-f5c17886746f, called with patientId=1046
composite_1   | 2016-05-16 13:31:03.088 DEBUG 1 --- [eIntegration-51] c.n.h.c.javanica.command.GenericCommand  : execute command: getPatient
patient_1     | 2016-05-16 13:31:03.110  INFO 1 --- [  XNIO-2 task-3] c.p.m.c.patient.service.PatientService   : /patient called
composite_1   | 2016-05-16 13:31:03.171 DEBUG 1 --- [eIntegration-52] c.n.h.c.javanica.command.GenericCommand  : execute command: getObservations
composite_1   | 2016-05-16 13:31:03.184  INFO 1 --- [eIntegration-52] c.p.m.c.p.s.PatientCompositeIntegration  : GetObservations...
observation_1 | 2016-05-16 13:31:03.209  INFO 1 --- [  XNIO-2 task-1] c.p.m.c.o.service.ObservationService     : /observation called, processing time: 0
observation_1 | 2016-05-16 13:31:03.231  INFO 1 --- [  XNIO-2 task-1] c.p.m.c.o.service.ObservationService     : /observation response size: 3
composite_1   | 2016-05-16 13:31:03.339 DEBUG 1 --- [eIntegration-53] c.n.h.c.javanica.command.GenericCommand  : execute command: getEpisodes
composite_1   | 2016-05-16 13:31:03.340  INFO 1 --- [eIntegration-53] c.p.m.c.p.s.PatientCompositeIntegration  : GetEpisodes...
episode_1     | 2016-05-16 13:31:03.372  INFO 1 --- [ XNIO-2 task-10] c.p.m.c.episode.service.EpisodeService   : /episodes called, processing time: 0
episode_1     | 2016-05-16 13:31:03.384  INFO 1 --- [ XNIO-2 task-10] c.p.m.c.episode.service.EpisodeService   : /episodes response size: 3
api_1         | 2016-05-16 13:31:03.445  INFO 1 --- [ntApiService-19] c.p.m.a.p.service.PatientApiService      : GetPatientComposite http-status: 200
...
episode_2     | 2016-05-16 13:31:04.176  INFO 1 --- [ XNIO-2 task-10] c.p.m.c.episode.service.EpisodeService   : /episodes called, processing time: 0
episode_2     | 2016-05-16 13:31:04.192  INFO 1 --- [ XNIO-2 task-10] c.p.m.c.episode.service.EpisodeService   : /episodes response size: 3
```

## Wrap-up
We have installed Docker and used its environment to run our services. We glanced at its capabilities using `docker-compose up` to start
all of our services with a single command, and `docker-compose scale` to scale our service.

In the [next](epsilon.html) article we will add an angularJS UI application to replace our cURL commands!


