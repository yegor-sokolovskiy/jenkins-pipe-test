#!/bin/bash

docker-compose up -d registry
docker-compose build jenkins-slave
docker tag slave-jenkins 127.0.0.1:5000/goshazzz/slave-jenkins:latest
docker tag slave-jenkins goshazzz/slave-jenkins:latest
docker push 127.0.0.1:5000/goshazzz/slave-jenkins:latest

