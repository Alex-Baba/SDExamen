docker build -t localhost:5000/monitor_microservice:v1 .
docker push localhost:5000/monitor_microservice:v1

docker run -d --name monitor_microservice --net=host localhost:5000/monitor_microservice:v1

