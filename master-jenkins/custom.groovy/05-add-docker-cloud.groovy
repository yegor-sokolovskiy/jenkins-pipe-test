import com.nirima.jenkins.plugins.docker.DockerCloud
import com.nirima.jenkins.plugins.docker.DockerTemplate
import com.nirima.jenkins.plugins.docker.DockerTemplateBase
import com.nirima.jenkins.plugins.docker.launcher.AttachedDockerComputerLauncher
import io.jenkins.docker.connector.DockerComputerAttachConnector
import jenkins.model.Jenkins

// parameters
def dockerTemplateBaseParameters = [
  bindAllPorts:       false,
  bindPorts:          '',
  cpuPeriod:          null,
  cpuQuota:           null,
  cpuShares:          null,
  dnsString:          '',
  dockerCommand:      '',
  environmentsString: '',
  extraHostsString:   '',
  hostname:           '',
  image:              'goshazzz/slave-jenkins:latest',
  macAddress:         '',
  memoryLimit:        null,
  memorySwap:         null,
  network:            '',
  privileged:         false,
  pullCredentialsId:  '',
  sharedMemorySize:   null,
  tty:                true,
  volumesFromString:  '',
  volumesString:      ''
]

def DockerTemplateParameters = [
  instanceCapStr: '4',
  labelString:    'jenkins-slave gcc',
  remoteFs:       '/home/jenkins'
]

def dockerCloudParameters = [
  connectTimeout:   3,
  containerCapStr:  '4',
  credentialsId:    '',
  dockerHostname:   '',
  name:             'docker2',
  readTimeout:      60,
  serverUrl:        'tcp://127.0.0.1:4243',
  version:          ''
]

// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerTemplateBase.java
DockerTemplateBase dockerTemplateBase = new DockerTemplateBase(dockerTemplateBaseParameters.image)
dockerTemplateBase.setPullCredentialsId(dockerTemplateBaseParameters.pullCredentialsId)
dockerTemplateBase.setDnsString(dockerTemplateBaseParameters.dnsString)
dockerTemplateBase.setNetwork(dockerTemplateBaseParameters.network)
dockerTemplateBase.setDockerCommand(dockerTemplateBaseParameters.dockerCommand)
dockerTemplateBase.setVolumesString(dockerTemplateBaseParameters.volumesString)
dockerTemplateBase.setVolumesFromString(dockerTemplateBaseParameters.volumesFromString)
dockerTemplateBase.setEnvironmentsString(dockerTemplateBaseParameters.environmentsString)
dockerTemplateBase.setHostname(dockerTemplateBaseParameters.hostname)
dockerTemplateBase.setMemoryLimit(dockerTemplateBaseParameters.memoryLimit)
dockerTemplateBase.setMemorySwap(dockerTemplateBaseParameters.memorySwap)
  //dockerTemplateBaseParameters.cpuPeriod,
  //dockerTemplateBaseParameters.cpuQuota,
dockerTemplateBase.setCpuShares(dockerTemplateBaseParameters.cpuShares)
  //dockerTemplateBaseParameters.sharedMemorySize,
dockerTemplateBase.setBindPorts(dockerTemplateBaseParameters.bindPorts)
dockerTemplateBase.setBindAllPorts(dockerTemplateBaseParameters.bindAllPorts)
dockerTemplateBase. setPrivileged(dockerTemplateBaseParameters.privileged)
dockerTemplateBase.setTty(dockerTemplateBaseParameters.tty)
dockerTemplateBase.setMacAddress(dockerTemplateBaseParameters.macAddress)
dockerTemplateBase.setExtraHostsString(dockerTemplateBaseParameters.extraHostsString)


// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerTemplate.java
DockerTemplate dockerTemplate = new DockerTemplate(
  dockerTemplateBase)
  new DockerComputerAttachConnector(),
  DockerTemplateParameters.labelString,
  DockerTemplateParameters.remoteFs,
  DockerTemplateParameters.instanceCapStr
)

// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerCloud.java
DockerCloud dockerCloud = new DockerCloud(
  dockerCloudParameters.name,
  [dockerTemplate],
  dockerCloudParameters.serverUrl,
  dockerCloudParameters.containerCapStr,
  dockerCloudParameters.connectTimeout,
  dockerCloudParameters.readTimeout,
  dockerCloudParameters.credentialsId,
  dockerCloudParameters.version,
  dockerCloudParameters.dockerHostname
)

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// add cloud configuration to Jenkins
jenkins.clouds.add(dockerCloud)

// save current Jenkins state to disk
jenkins.save()