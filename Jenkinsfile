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
                    sh './mvnw clean install -DskipTests=true' 
                }
            }
        }

        stage('Construir y Ejecutar contenedor') {
            steps {
                script {
                    sh 'pwd'  // Verifica que estás en el directorio correcto
                    sh 'ls -la'  // Verifica si mvnw está presente en el directorio actual
                    sh 'docker-compose up --build'  
                }
            }
        }

        stage('Pruebas') {
            steps {
                script {
                    // Ejecutar las pruebas dentro del contenedor si es necesario
                    sh './mvnw test'  // O puedes ejecutar pruebas en el contenedor según lo requieras
                }
            }
        }
    }

    post {
        always {
            // Limpiar los contenedores después de las pruebas
            sh 'docker-compose down'  // Detener y eliminar los contenedores
            cleanWs()  // Limpiar el espacio de trabajo de Jenkins
        }
    }
}
