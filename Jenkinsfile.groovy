#!/usr/bin/env groovy

/**
        * Sample Jenkinsfile for Jenkins Pipeline       
 */

#!/usr/bin/env groovy


node {
    stage ('Stage 1 Checkout Repository') {         
        checkout scm
        sh 'ls -l'
        if (env.BRANCH_NAME == 'master') {
            echo 'I only execute on the master branch'
        } else {
            echo 'I execute elsewhere'
        }

        git 'https://github.com/yegor-sokolovskiy/hello-world.git'
        sh 'ls -l'
    }
    stage ('Stage 2 Build') {
        sh 'make'
        sh 'ls -l'
    }
    stage ('Stage 3 Run') {
        sh './hello'
    }
} 