pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                
                checkout([$class: 'GitSCM', branches: [[name: '*/develop']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend.git']]])
            }
        }

        stage('Compilación') {
            steps {
                script {
                    sh './mvnw clean install -DskipTests=true'  // O usa 'mvn clean install' si ya tienes Maven instalado
                }
            }
        }

        stage('Pruebas') {
            steps {
                script {
                    sh './mvnw test'  
                }
            }
        }
    }

    post {
        always {
            
            cleanWs()
        }
    }
}
