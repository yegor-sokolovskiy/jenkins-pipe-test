#!groovy

// imports
import hudson.plugins.git.*
import hudson.plugins.git.extensions.*
import hudson.plugins.git.extensions.impl.*
import jenkins.model.Jenkins

// parameters
def jobParameters = [
  name:          'Curl_n_Test',
  description:   'Building and testing Curl sources from https://github.com/curl/curl according with Jenkinsfile from Jenkins-Pipe-Test repo, branch master. \
Repo provided by SCM from GitHub https://github.com/yegor-sokolovskiy/jenkins-pipe-test.git with anonymous access',
  repository:     'https://github.com/yegor-sokolovskiy/jenkins-pipe-test.git',
  branch:         '*/master'
]

// define repo configuration
def branchConfig                =   [new BranchSpec(jobParameters.branch)]
def userConfig                  =   [new UserRemoteConfig(jobParameters.repository, null, null, jobParameters.credentialId)]
def cleanBeforeCheckOutConfig   =   new CleanBeforeCheckout()
def cloneConfig                 =   new CloneOption(true, true, null, 3)
def extensionsConfig            =   [cleanBeforeCheckOutConfig,cloneConfig]
def scm                         =   new GitSCM(userConfig, branchConfig, false, [], null, null, extensionsConfig)

// define SCM flow
def flowDefinition = new org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition(scm, "Jenkinsfile")

// set lightweight checkout
flowDefinition.setLightweight(true)

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// create the job
def job = new org.jenkinsci.plugins.workflow.job.WorkflowJob(jenkins,jobParameters.name)

// define job type
job.definition = flowDefinition

// set job description
job.setDescription(jobParameters.description)
job.logRotator = new hudson.tasks.LogRotator(7,5)

// save to disk
jenkins.save()
jenkins.reload()