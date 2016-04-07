#!/usr/bin/env bash

cf start auth-server
cf start discovery-server
cf start edge-server
cf start monitor-dashboard
cf start patient-api-service
cf start patient-composite-service
cf start patient-service
cf start observation-service
cf start episode-service
cf start turbine

cf apps
