# jenkins-pipe-test

## Jenkins Pipeline Test

Experemental project using testing practices for Jenkins Pipelines

### init-script.sh

File `init-script.sh` is needed in case, when you want to use local registry of docker images. Run it first for starting `registry` container, build `slave-jenkins` container and push it to local registry. Otherwise image [goshazzz/slave-jenkins:latest](https://hub.docker.com/repository/docker/goshazzz/slave-jenkins) from dockers hub will be used.

### Description

This project use Docker for running Jenkins instance in `master-jenkins` container. The new job for Curl building will create at starting this container with groovy script [10-addjob-curl-scm](https://github.com/yegor-sokolovskiy/jenkins-pipe-test/blob/master/master-jenkins/custom.groovy/10-addjob-curl-scm.groovy). Curl's bulding processes, including unit tests, are running at `slave-jenkins` container. 
Also, this job contain pre-steps for self-testing with [JenkinPipelineUnit](https://github.com/jenkinsci/JenkinsPipelineUnit) framework. All these steps described in [Jenkinsfile](https://github.com/yegor-sokolovskiy/jenkins-pipe-test/blob/master/Jenkinsfile) at root directory of the project.

### How to use

For first time running is necessary to build `master-jenkins` image:
    docker-compose build jenkins-master

After image was built, run Jenkins instance:
    docker-compose up -d jenkins-master

Few minutes later it's possible to connect to Jenkins GUI via `http://dockerhost:8081`
The job "Curl_n_Test" will appear. Run and enjoy!



