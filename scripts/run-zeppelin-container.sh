
echo "running apache zeppelin container using podman"

podman run -d -p 8080:8080 --name local-zeppelin apache/zeppelin:0.9.0


