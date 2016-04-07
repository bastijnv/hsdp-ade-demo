#!/usr/bin/env bash

cf stop auth-server
cf stop discovery-server
cf stop edge-server
cf stop monitor-dashboard
cf stop patient-api-service
cf stop patient-composite-service
cf stop patient-service
cf stop observation-service
cf stop episode-service
cf stop turbine

cf apps
