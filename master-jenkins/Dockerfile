FROM jenkins/jenkins:lts

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

ARG HTTP_PORT=8080
ARG AGENT_PORT=50000
ARG DOCKER_COMPOSE_VERSION=1.27.4

USER root

RUN apt-get update && \
   apt-get upgrade -y && \
   apt-get -y install apt-transport-https \
      ca-certificates \
      curl \
      gnupg2 \
      git \
      software-properties-common && \
   curl -fsSL https://download.docker.com/linux/$(. /etc/os-release; echo "$ID")/gpg > /tmp/dkey; apt-key add /tmp/dkey && \
   add-apt-repository \
      "deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") \
      $(lsb_release -cs) \
      stable" && \
   apt-get update && \
   apt-get -y install docker-ce && \
   apt-get clean autoclean && apt-get autoremove && rm -rf /var/lib/{apt,dpkg,cache,log}/

RUN sed -i 's%^ExecStart=/usr/bin/dockerd.*%ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:4243 -H unix:///var/run/docker.sock%' /lib/systemd/system/docker.service

EXPOSE ${HTTP_PORT}
EXPOSE ${AGENT_PORT}

COPY master-jenkins/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

COPY master-jenkins/custom.groovy/*.groovy /usr/share/jenkins/ref/init.groovy.d/
#COPY docker-compose.yml /opt/docker-jenkins/
#COPY slave-jenkins /opt/docker-jenkins/slave-jenkins

#RUN curl -L https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose 

#RUN cd /opt/docker-jenkins && docker-compose up jenkins-slave -d
#RUN docker tag slave-jenkins 127.0.0.1:5000/slave-jen:latest && docker push 127.0.0.1:5000/slave-jen:latest

RUN usermod -aG docker jenkins && gpasswd -a jenkins docker

USER jenkins