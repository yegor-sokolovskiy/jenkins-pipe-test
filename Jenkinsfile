#!/usr/bin/env groovy

println "Run Jenkinsfile"

node {   
    stage ('PreStep 0') {
        checkout scm
    }
    stage ('PreStep 1') {
        sh "./gradlew clean test"
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
            sh 'make' 
            maketext = readFile(file: "./tests/Makefile").replace('TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) $(srcdir)/runtests.pl', 
                                   'TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS)')
            writeFile(file: "./tests/Makefile", text: maketext)
            sh 'make test'            
        }
        stage ('Stage 4 Run Unit Tests') {            
            def skipUnits = [1307, 1330, 1660]
            def mapAllUnits = [:]
            def unitFiles = findFiles(glob: "tests/unit/unit*")
            unitFiles.each { f ->
                println f.name
                s = (f.name =~ /unit(\d+)/)[0]
                mapAllUnits[s[1]] = s[0]
            }

            mapAllUnits.each { key, val ->               
                if (!skipUnits.contains(key.toInteger())) {
                    println "-----------Test Num $val---------------"
                    dir("tests") {
                        out = sh(returnStdout: true, script: "perl runtests.pl $key | grep -E \"TESTDONE|TESTFAIL|TESTINFO\"" )
                    }
                    println out                    
                    if (out && out.contains("TESTFAIL")) {
                        sh "exit 1" 
                    }           
                }
                
            }
        }
        stage ('Stage 5 Prepare Artifacts') {            
            String workDirName = "${WORKSPACE}"
            String outDirName  = "/out"
            String artifactsDirName = "root"
            String fullDirName = workDirName  + outDirName + "/" + artifactsDirName
            sh "make install exec_prefix=$fullDirName prefix=$fullDirName"
            sh "tar -zcvf ${workDirName}/artifacts.tar.gz -C ${workDirName}${outDirName}/ ${artifactsDirName}"
            archiveArtifacts artifacts: '**/artifacts.tar.gz'
        }
    }
}