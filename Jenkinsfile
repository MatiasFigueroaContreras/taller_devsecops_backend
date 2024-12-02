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

           stage('Construir y Ejecutar contenedor') {
            steps {
                script {
                    bat 'docker-compose up --build -d'  // Ejecuta docker-compose usando 'bat' en Windows
                }
            }
        }

        // Etapa para esperar que ZAP esté listo
        stage('Esperar a que ZAP esté listo') {
            steps {
                script {
                    // Intentar hasta 30 veces para ver si ZAP está listo
                    def zapReady = false
                    for (int i = 0; i < 30; i++) {
                        try {
                            def response = bat(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:8081", returnStdout: true).trim()
                            if (response == "200") {
                                zapReady = true
                                break
                            }
                        } catch (Exception e) {
                        }
                        sleep(10)  
                    }
                    if (!zapReady) {
                        error("ZAP no está disponible después de varios intentos.")
                    }
                }
            }
        }

        stage('Ejecutar OWASP ZAP') {
            steps {
                script {
                    bat 'docker exec -t owasp_zap zap-baseline.py -t http://localhost:8090'  
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
