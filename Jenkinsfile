#!/usr/bin/env groovy

//properties([buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '5'))])

println "Run Jenkinsfile"

node {   
    stage ('PreStep 0') {
        checkout scm
    }
    stage ('PreStep 1') {
        sh "./gradlew test"
    }    
    docker.image('goshazzz/slave-jenkins').inside {
        stage ('Stage 1 Checkout Repository') {
            //deleteDir()
            git 'https://github.com/curl/curl.git'
            sh 'ls -l'
        }
        stage ('Stage 2 Prebuild') {
            sh 'autoreconf -fi'
            sh './configure --enable-debug'
        }
        stage ('Stage 3 Build') {
            sh '''#!/usr/bin/env bash
                make 
                sed -i 's%^TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) $(srcdir)/runtests.pl%TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS)%' ./tests/Makefile
                make test
            '''
        }
        stage ('Stage 4 Run Unit Tests') {
            sh '''#!/usr/bin/env bash
                cd ${WORKSPACE}/tests
                TEST_NUM=$(ls ./unit | grep -E '^unit[[:digit:]]*$' | sed 's/unit\\(.*\\)/\\1/')
                for TEST in $TEST_NUM
                do
                    TEST_RESULTS=$(perl ./runtests.pl $TEST | grep -E "TESTDONE|TESTFAIL|TESTINFO")
                    echo "-----------Test Num $TEST---------------"
                    echo $TEST_RESULTS
                done
            '''
        }
        stage ('Stage 5 Prepare Artifacts') {
            sh '''#!/usr/bin/env bash
                OUT_DIR="/out"
                ARTIFACTS_DIR="root"
                OUT_FULL_DIR=${WORKSPACE}${OUT_DIR}/${ARTIFACTS_DIR}
                mkdir -p $OUT_FULL_DIR
                make install exec_prefix=$OUT_FULL_DIR prefix=$OUT_FULL_DIR
                tar -zcvf ${WORKSPACE}/artifacts.tar.gz -C ${WORKSPACE}${OUT_DIR}/ ${ARTIFACTS_DIR}
            '''
            archiveArtifacts artifacts: '**/artifacts.tar.gz'
        }
    }
}