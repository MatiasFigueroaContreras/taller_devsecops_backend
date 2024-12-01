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
                    bat 'docker-compose up --build'  // Ejecuta docker-compose usando 'bat' en Windows
                }
            }
        }

        stage('Pruebas') {
            steps {
                script {
                    bat 'mvn test'  // Ejecuta las pruebas con Maven en Windows
                }
            }
        }
    }

    post {
        always {
            // Limpiar los contenedores después de las pruebas
            bat 'docker-compose down'  // Detener y eliminar los contenedores usando 'bat' en Windows
            cleanWs()  // Limpiar el espacio de trabajo de Jenkins
        }
    }
}
