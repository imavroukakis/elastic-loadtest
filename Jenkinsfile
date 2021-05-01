def gitUrl = 'https://github.com/imavroukakis/elastic-loadtest'

def testGroups = [:]
def numberOfTestNodes = 5
def splitTestsAbove = 50.0
def jdkTool = 'openjdk-11'
def sbtTool = '1.3.8'
def loadTestBinary = 'loadtest'
def loadTestVersion = loadTestBinary + '-1.0'

pipeline {
    agent any
    tools {
        jdk jdkTool
    }
    environment {
        SBT_HOME = tool name: sbtTool, type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'
        PATH = "${env.SBT_HOME}/bin:${env.PATH}"
        CREDENTIALS = credentials('elastic-search-password')
    }

    stages {
        stage('Checkout') {
            steps {
                buildDescription "Users/sec:${params.usersPerSecond}\n Duration:${params.duration}\n Index:${params.indexName}"
                deleteDir()
                git branch: 'main', poll: false, url: "$gitUrl"
            }
        }
        stage('Build') {
            steps {
                ansiColor('xterm') {
                    sh "sbt clean compile packArchiveTgz"
                }
                stash name: 'load-test', includes: "target/${loadTestVersion}.tar.gz"
            }
        }
        stage('Load Test') {
            steps {
                script {                    
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
                                def useEsResponseTime = "${params.useEsResponseTime}" ? '--use-es-response-time ' : ''
                                deleteDir()
                                unstash 'load-test'
                                sh "mv target/${loadTestVersion}.tar.gz ./"
                                sh "tar xf ${loadTestVersion}.tar.gz"
                                sh "JAVA_HOME=$javaHome ${loadTestVersion}/bin/${loadTestBinary} --elastic-url=" +
                                        "${params.elasticUrl} " +
                                        "--username=elastic " +
                                        '--password=$CREDENTIALS ' +
                                        "--index-name=${params.indexName} " +
                                        "--users-per-second=$usersPerNodeCount " +
                                        "--test-duration=\"${params.duration}\" " +
                                        "$useEsResponseTime" +
                                        "--heart-attack-and-stroke-search"
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
                sh "mv target/${loadTestVersion}.tar.gz ./ && tar xvf ${loadTestVersion}.tar.gz"
                sh "${loadTestVersion}/bin/${loadTestBinary} --report-only \"${env.WORKSPACE}/results\""
                sh "mv results results-test-${env.BUILD_NUMBER}"
                gatlingArchive()
                sh "tar zcvf results-test-${env.BUILD_NUMBER}.tar.gz results-test-${env.BUILD_NUMBER}"
                archiveArtifacts artifacts: "results-test-${env.BUILD_NUMBER}.tar.gz", caseSensitive: false, onlyIfSuccessful: true
            }
        }
    }
}
