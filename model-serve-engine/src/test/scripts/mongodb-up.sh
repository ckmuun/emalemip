#!/bin/bash

echo 'starting mongodb docker compose file for local testing'

# note that we are using podman here so we dont need to sudo this command.
podman-compose --file mongodb-docker-compose.yml up

# smoketest mongodb container
curl --request  -sL \
     --url 'localhost:8081'\

echo 'done.. mongo available'
