pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                
                git 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend'  
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
