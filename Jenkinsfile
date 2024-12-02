pipeline {
    agent any

    tools {
        maven 'Maven3'  // Especifica el nombre de la instalación de Maven configurada en Jenkins
        jdk 'jdk17'  
    }

    parameters {
        choice choices: ['Baseline', 'APIS', 'Full'],
            description: 'Type of scan that is going to perform inside the container',
            name: 'SCAN_TYPE'
            
        string defaultValue: 'http://localhost:3000',
            description: 'Target URL to scan',
            name: 'TARGET'
            
        booleanParam defaultValue: true,
            description: 'Parameter to know if you want to generate a report.',
            name: 'GENERATE_REPORT'
    }

    stages {
        stage('Checkout Backend') {
    steps {
        checkout([$class: 'GitSCM', branches: [[name: 'refs/heads/develop']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend.git']]])
    }
}


        stage('Analisis Estatico con Semgrep') {
            steps {
                script {
                    bat """
                    docker run -e SEMGREP_APP_TOKEN=${env.SEMGREP_APP_TOKEN} --rm -v "%cd%:/src" semgrep/semgrep semgrep ci --json --output /src/semgrep_report.json
                    """
                }
            }
        }

        stage('Archivar Reporte Semgrep') {
            steps {
                archiveArtifacts artifacts: '**/semgrep_report.json', allowEmptyArchive: true
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

        // FRONTEND STAGES /////////////////////////////////////////////////

        stage('Checkout Frontend Repository') {
    steps {
        script {
            dir('frontend') {
                checkout([
                    $class: 'GitSCM', 
                    branches: [[name: 'refs/heads/develop']],  // Usar refs/heads en lugar de */
                    extensions: [], 
                    userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_frontend.git']]
                ])
            }
        }
    }
}


        stage('Generar variables de entorno frontend') {
            steps {
                script {
                    dir('frontend') {
                        // Crear el archivo .env con los valores necesarios
                        writeFile file: '.env', 
                        text: '''
                        NEXT_PUBLIC_API_URL=http://localhost:8090
                        NEXTAUTH_SECRET=kDIoxobrY2ut97pwem58BNzVMxAhHXzI96A2vNLlM78=
                        NEXTAUTH_URL=http://localhost:3000
                        '''
                    }
                }
            }
        }

        stage('Ejecutar Docker Compose para Frontend') {
            steps {
                script {
                    dir('frontend') {
                        bat 'docker-compose up --build -d'
                    }
                }
            }
        }

        // Escaneo OWASP ZAP
stage('Ejecutar OWASP ZAP y generar reporte JSON') {
    steps {
        script {
            // Asegurarte de que el valor de params.TARGET esté presente
            echo "Escaneando la URL: ${params.TARGET}"
            
            // Usar el valor de la URL directamente sin % para variables en el entorno de Jenkins
            bat """
            docker exec -t owasp_zap /zap/zap.sh -cmd -quickurl ${params.TARGET} -quickout /zap/reports/zap-report.json
            """
        }
    }
}


        // Copiar reporte a la raíz del proyecto
        stage('Copiar Reporte JSON a la raíz del proyecto') {
            steps {
                script {
                    // Copiar el archivo de reporte JSON desde el contenedor a la raíz del proyecto en Jenkins
                    bat 'docker cp owasp_zap:/zap/reports/zap-report.json %WORKSPACE%/zap-report.json'
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
