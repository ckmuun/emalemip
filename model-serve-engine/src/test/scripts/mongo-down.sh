#!/bin/bash

echo 'tearing down local test mongodb'
podman-compose --file mongodb-docker-compose.yml down
