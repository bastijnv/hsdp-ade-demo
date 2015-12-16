#!/usr/bin/env bash

cd microservices/core/patient-service;                ./gradlew clean publishToMavenLocal; cd -
cd microservices/core/observation-service;         ./gradlew clean publishToMavenLocal; cd -
cd microservices/core/episode-service;                 ./gradlew clean publishToMavenLocal; cd -
cd microservices/composite/patient-composite-service; ./gradlew clean build; cd -

cd microservices/support/discovery-server;            ./gradlew clean build; cd -
cd microservices/support/edge-server;                 ./gradlew clean build; cd -
