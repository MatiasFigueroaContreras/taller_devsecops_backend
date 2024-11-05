pipeline {
    agent any
    tools {
        maven "maven"
    }
    stages {
        stage("Build JAR FILE"){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins_ssh_deploy_key', url: 'git@github.com:MatiasFigueroaContreras/tingeso_evaluacion01.git']])
                dir("evaluacion01"){
                    sh "mvn clean install"
                }
                
            }
        }
        stage("Test"){
            steps{
                dir("evaluacion01"){
                    sh "mvn test"
                }
            }
        }
        stage("SonarQube Analysis"){
            steps{
                dir("evaluacion01"){
                    sh "mvn sonar:sonar -Dsonar.projectKey=tingeso_evaluacion01 -Dsonar.projectName='tingeso_evaluacion01' -Dsonar.host.url=http://localhost:9000 -Dsonar.token=sqp_b41a06d5711488b9bd8210ccc7eb215de3fca71b"
                }
            }
        }
        stage("Build Docker Image"){
            steps{
                dir("evaluacion01"){
                    sh "docker build -t matiasfc/tingeso_evaluacion01 ."
                }
            }
        }
        stage("Push Docker Image"){
            steps{
                dir("evaluacion01"){
                    withCredentials([string(credentialsId: 'dckr_hub_password', variable: 'dckr_pass')]) {
                        sh "docker login -u matiasfc -p ${dckr_pass}"
                    }
                    sh "docker push matiasfc/tingeso_evaluacion01"
                }
            }
        }
    }
    post{
        always{
            dir("evaluacion01"){
                sh "docker logout"
            }
        }
    }
}
