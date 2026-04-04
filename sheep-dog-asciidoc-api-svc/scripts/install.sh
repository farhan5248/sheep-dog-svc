#!/bin/bash
cd "$(dirname "$0")/.."
kubectl config use-context minikube
mvn clean install
