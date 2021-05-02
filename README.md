# Kotlin Spring Kafka Request-Reply

Demo project for implementation Request-Reply pattern with Kafka message broker

### Run 

## Start Kafka in docker container (kafka-client/docker-compose.yml)
```
docker-compose up
```

## Run kafka-client and kafka-server and then send http request like this
```
curl -X GET http://localhost:1230/request/1
```
