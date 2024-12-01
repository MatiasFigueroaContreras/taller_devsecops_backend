pipeline {
    agent any

  stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/develop']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend.git']]])
            }
        }

        stage('Generar secrets.properties') {
            steps {
                script {
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
                sh 'pwd'  // Verifica que estás en el directorio correcto
                sh 'ls -la'  // Verifica si mvnw está presente en el directorio actual
                sh './mvnw clean install -DskipTests=true'  // Ejecuta Maven usando el script mvnw
            }
        }

        stage('Construir y Ejecutar contenedor') {
            steps {
                sh 'docker-compose up --build'  // Construye y ejecuta el contenedor
            }
        }

        stage('Pruebas') {
            steps {
                sh './mvnw test'  // Ejecuta las pruebas con Maven usando el script mvnw
            }
        }
    }

    post {
        always {
            sh 'docker-compose down'  // Detener y eliminar los contenedores
            cleanWs()  // Limpiar el espacio de trabajo de Jenkins
        }
    }
}
