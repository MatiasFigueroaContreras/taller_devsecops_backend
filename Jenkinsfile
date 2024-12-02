pipeline {
    agent any

  tools {
        maven 'Maven3'  // Especifica el nombre de la instalación de Maven configurada en Jenkins
        jdk 'jdk17'  
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/develop']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend.git']]])
            }
        }

        stage('Generar secrets.properties') {
            steps {
                script {
                    // Crear el archivo secrets.properties con los valores necesarios
                    writeFile file: 'secrets.properties', text: '''admin.password=password
                    admin.correo=correo@gmail.com
                    admin.nombre=nombre
                    admin.apellido_paterno=paterno
                    admin.apellido_materno=materno
                    jwt.secret_key=miClaveSecretaDe32Caracteres0000miClaveSecretaDe32Caracteres0000miClaveSecretaDe32Caracteres0000miClaveSecretaDe32Caracteres0000'''
                }
            }
        }


            stage('Docker Login') {
            steps {
                script {
                    // Inicia sesión en Docker Hub
                    bat 'docker login -u gaspitas241 -p morrowind241'
                }
            }
        }
        stage('Compilación') {
            steps {
                script {
                    bat 'echo %cd%'  // En lugar de 'sh', usa 'bat' para ejecutar comandos en Windows
                    bat 'dir'  // En lugar de 'ls -la', usa 'dir' para listar los archivos en Windows
                    bat 'mvn clean install -DskipTests=true'  // Ejecutar Maven en Windows
                }
            }
        }

        stage('Construir y Ejecutar contenedor') {
            steps {
                script {
                    bat 'docker-compose up --build -d'  // Ejecuta docker-compose usando 'bat' en Windows
                }
            }
        }
            stage('Ejecutar OWASP ZAP') {
                steps {
                    script {
                        // Ejecutar el escaneo de ZAP utilizando el comando adecuado
                        bat 'docker exec -t owasp_zap zap -cmd -quickurl http://localhost:8090'
                    }
                }
            }

    
    }

    post {
        always {
            bat 'docker-compose down'  
            cleanWs()  
        }
    }
}
