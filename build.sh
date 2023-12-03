#!/bin/sh

mvn --projects :api-monitor-orchestrator --also-make clean package -DskipTests

docker build -t api-monitor/api-monitor-orchestrator -t api-monitor/api-monitor-standalone . -f Dockerfile.orchestrator

mvn --projects :api-monitor-runner --also-make clean package -DskipTests

docker build -t api-monitor/api-monitor-runner . -f Dockerfile.runner
