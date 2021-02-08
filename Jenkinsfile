def gitUrl = 'https://github.com/imavroukakis/loadtest.git'

def testGroups = [:]
def numberOfTestNodes = 5
def gitCredentials = 'Github'
def splitTestsAbove = 50.0
def jdkTool = 'openjdk-11'
def sbtTool = '1.3.8'
def loadTestBinary = 'loadtest-1.0'

pipeline {
    tools {
        jdk jdkTool
    }
    environment {
        SBT_HOME = tool name: sbtTool, type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'
        PATH = "${env.SBT_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                git branch: 'main', credentialsId: "$gitCredentials", poll: false, url: "$gitUrl"
            }
        }
        stage('Build') {
            steps {
                sh "sbt clean compile packArchive"
                stash name: 'load-test', includes: "target/${loadTestBinary}.tar.gz"
            }
        }
        stage('Load Test') {
            steps {
                script {
                    currentBuild.description = "Users/sec:${params.usersPerSecond}/Duration:${params.duration}"
                    def userPerSecond = "${params.usersPerSecond}" as Double
                    int usersPerNodeCount
                    if (userPerSecond >= splitTestsAbove) {
                        usersPerNodeCount = Math.round(userPerSecond / numberOfTestNodes)
                    } else {
                        usersPerNodeCount = userPerSecond
                        numberOfTestNodes = 1
                    }
                    for (int i = 0; i < numberOfTestNodes; i++) {
                        def num = i
                        testGroups["node $num"] = {
                            node {
                                def javaHome = tool name: jdkTool
                                deleteDir()
                                unstash 'load-test'
                                sh "mv target/${loadTestBinary}.tar.gz ./"
                                sh "tar xf ${loadTestBinary}.tar.gz"
                                sh "JAVA_HOME=$javaHome ${loadTestBinary}/bin/load-test --elastic-url=" +
                                        "${params.elasticUrl} " +
                                        "--username=elastic " +
                                        "--password=${params.password} " +
                                        "--index-name=${params.indexName} " +
                                        "--users-per-second=$usersPerNodeCount " +
                                        "--test-duration=${params.duration} " +
                                        "--use-es-response-time=${params.useEsResponseTime} " +
                                        "--heart-attack-and-stroke-test"
                                stash name: "node $num", includes: '**/simulation.log'
                            }
                        }
                    }
                    parallel testGroups
                }
            }
        }
        stage('Collect results') {
            steps {
                script {
                    if (testGroups.containsKey("default")) {
                        unstash 'default'

                    } else {
                        for (int i = 0; i < numberOfTestNodes; i++) {
                            def num = i
                            unstash "node $i"
                        }
                    }
                }
                sh "mv target/${loadTestBinary}.tar.gz ./ && tar xvf ${loadTestBinary}.tar.gz"
                sh "${loadTestBinary}/bin/load-test --report-only \"${env.WORKSPACE}/results\""
                sh "mv results results-test-${params.searchEnv}-${env.BUILD_NUMBER}"
                sh "tar zcvf results-test-${params.searchEnv}-${env.BUILD_NUMBER}.tar.gz results-test-${params.searchEnv}-${env.BUILD_NUMBER}"
                archiveArtifacts artifacts: "results-test-${params.searchEnv}-${env.BUILD_NUMBER}.tar.gz", caseSensitive: false, onlyIfSuccessful: true
            }
        }
    }
}
