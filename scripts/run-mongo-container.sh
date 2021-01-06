echo "starting local mongodb container using podman"

podman run -d -p 27017:27017 --name local-mongo mongo
