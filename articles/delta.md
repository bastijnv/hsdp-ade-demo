---
title: Delta - Dockerize!
layout: default-article
---

{% include toc.html %}

## Introduction
Docker containers wrap up a piece of software in a complete filesystem that contains everything it needs to run: code, runtime, system tools, system libraries – anything you can install on a server. This guarantees that it will always run the same, regardless of the environment it is running in. 
They are based on open standards allowing containers to run on all major Linux distributions and Microsoft operating systems with support for every infrastructure.

Containers running on a single machine all share the same operating system kernel so they start instantly and make more efficient use of RAM. Images are constructed from layered filesystems so they can share common files, making disk usage and image downloads much more efficient. Containers isolate applications from each other and the underlying infrastructure while providing an added layer of protection for the application.

Compared to VMs Docker containers have similar resource isolation and allocation benefits as virtual machines but a different architectural approach allows them to be much more portable and efficient.

![Docker](../images/delta-docker.png)

In addition, Docker adds a lot to the developer experience via [Docker Machine](https://docs.docker.com/machine/), [Docker Compose](https://docs.docker.com/compose/), and [Docker Swarm](https://docs.docker.com/swarm/). Docker Machine is a tool that lets you install Docker Engine on virtual hosts, and manage the hosts with docker-machine commands. You can use Machine to create Docker hosts on your local Mac or Windows box, on your company network, in your data center, or on cloud providers like AWS or Digital Ocean. Compose is a tool for defining and running multi-container Docker applications. Docker Swarm is native clustering for Docker. It turns a pool of Docker hosts into a single, virtual Docker host.

Please see the Docker [website](https://www.docker.com/) for more information.

## Delta Overview
Soon to come.

## Docker Installation
Please install and setup Docker for your environment following the [Get Started](https://docs.docker.com/mac/started/) for Mac or [Get Started](https://docs.docker.com/windows/started) for windows. Return once you have Docker installed.

If you are on Windows or Mac, Docker by default creates a VM for you named *default*. For this series we will create our own VM with slightly changed 
specifications. Create a virtual machine named *dev* with 4096MB memory and 32GB disk size with the command below. After that start the new machine.
and initialize your command window. The final command on windows can be run without the `eval`. To test if everything succeeded run `docker ps`, it should
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

For convenience we map the IP of our dev box to our hosts file.

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

