#!/usr/bin/env groovy
import groovy.io.FileType
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
            sh 'make' 
            String workDirName = "${WORKSPACE}"
            println "workDirName is $workDirName"
            String makefileName  = workDirName + "/tests/Makefile"
            println "makefileName is $makefileName"           
            def makefile = new File(makefileName) //we currently in ./autotest dir, thats why ../
            def maketext = makefile.text
            makefile.withWriter { w ->
            w << maketext.replace('TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) $(srcdir)/runtests.pl', 
                                  'TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) $(srcdir)')
            }
            sh 'make test'            
        }
        stage ('Stage 4 Run Unit Tests') {            
            def skipUnits = [1307, 1330, 1660]
            String workDirName = "${WORKSPACE}"
            println "workDirName is $workDirName"
            String unitDirName  = workDirName + "/tests/unit"
            println "unitDirName is $unitDirName"
            def unitDir = new File(unitDirName)            
            def mapAllUnits = [:]
            unitDir.eachFileMatch (FileType.FILES, ~/^unit\d+$/) { f ->           
                s = (f =~ /unit(\d+)/)[0]                
                mapAllUnits[s[1]] = s[0]
            }
            mapAllUnits.each { key, val ->               
                if (!skipUnits.contains(key.toInteger())) {
                    println "-----------Test Num $val---------------"
                    out = sh(returnStdout: true, script: "perl ./tests/runtests.pl $val | grep -E \"TESTDONE|TESTFAIL|TESTINFO\"" )
                    println out                    
                    if (out && out.contains("TESTFAIL")) {
                        sh "exit 1" 
                    }           
                }
                
            }
        }
        stage ('Stage 5 Prepare Artifacts') {            
            String workDirName = "${WORKSPACE}"
            println "workDirName is $workDirName"
            String outDirName  = "/out"
            println "outDirName is $outDirName"          
            String artifactsDirName = "root"
            println "artifactsDirName is $artifactsDirName"
            String fullDirName = workDirName  + outDirName + "/" + artifactsDirName
            println "fullDirName is $fullDirName"
            def fullDir = new File(fullDirName)
            fullDir.mkdir()
            sh "make install exec_prefix=$fullDirName prefix=$fullDirName"
            sh "tar -zcvf ${workDirName}/artifacts.tar.gz -C ${workDirName}${outDirName}/ ${artifactsDirName}"
            archiveArtifacts artifacts: '**/artifacts.tar.gz'
        }
    }
}