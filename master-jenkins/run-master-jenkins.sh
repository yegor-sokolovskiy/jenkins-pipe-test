sudo docker run -d --privileged --name master-container -u 0 -p 8081:8080 -p 50000:50000 -v /var/lib/jenkins:/var/jenkins_home master-jenkins
