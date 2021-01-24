#!/bin/bash
echo "installing helm charts in directory"
echo "this is intended to work with a local helm installation"

# install apache pulsar repo into local helm
helm add repo https://pulsar.apache.org/charts
helm repo update

cd messaging-infra || exit
echo "pulling dependencies of Chart.yaml"
helm dependency update
cd ..

echo "doing the install"
helm install messaging-infrastructure --create-namespace messaging-infra
