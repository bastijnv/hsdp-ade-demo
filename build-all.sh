#!/usr/bin/env bash

cd util;                                              ./gradlew clean publishToMavenLocal; cd -

cd microservices/core/patient-service;                ./gradlew clean publishToMavenLocal build distDocker; cd -
cd microservices/core/observation-service;            ./gradlew clean publishToMavenLocal build distDocker; cd -
cd microservices/core/episode-service;                ./gradlew clean publishToMavenLocal build distDocker; cd -
cd microservices/composite/patient-composite-service; ./gradlew clean build distDocker; cd -
cd microservices/api/patient-api-service;             ./gradlew clean build distDocker; cd -

cd microservices/support/auth-server;                 ./gradlew clean build distDocker; cd -
cd microservices/support/discovery-server;            ./gradlew clean build distDocker; cd -
cd microservices/support/edge-server;                 ./gradlew clean build distDocker; cd -
cd microservices/support/monitor-dashboard;           ./gradlew clean build distDocker; cd -
cd microservices/support/turbine;                     ./gradlew clean build distDocker; cd -

find . -name *SNAPSHOT.jar -exec du -h {} \;
