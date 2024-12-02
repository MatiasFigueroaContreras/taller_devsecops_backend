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
        stage("Checkout Backend Repository") {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM', 
                        branches: [[name: '*/pipeline-test-mfc']], 
                        extensions: [], 
                        userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend.git']]
                    ])
                }
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

        // stage('Se compila el backend') {
        //     steps {
        //         script {
        //             bat 'echo %cd%'  // En lugar de 'sh', usa 'bat' para ejecutar comandos en Windows
        //             bat 'dir'  // En lugar de 'ls -la', usa 'dir' para listar los archivos en Windows
        //             bat 'echo %JAVA_HOME%'  // Imprime la variable de entorno JAVA_HOME
        //             bat 'java -version'  // Imprime la versión de Java
        //             bat 'mvn -v'  // Imprime la versión de Maven
        //             bat 'mvn clean install -DskipTests=true'  // Ejecutar Maven en Windows
        //         }
        //     }
        // }

        stage('Construir y Ejecutar contenedor backend') {
            steps {
                script {
                    bat 'docker-compose up --build -d'  // Ejecuta docker-compose usando 'bat' en Windows
                }
            }
        }

        stage('Checkout Frontend Repository') {
            steps {
                script {
                    dir('frontend') {
                        checkout([
                            $class: 'GitSCM', 
                            branches: [[name: '*/develop']], 
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

        stage('Pull and Run OWASP ZAP Docker Image') {
            steps {
                script {
                    bat 'docker pull ghcr.io/zaproxy/zaproxy:stable'
                    bat 'docker run -dt --name owasp ghcr.io/zaproxy/zaproxy:stable /bin/bash -p 9090:8080'
                    // bat 'docker exec owasp mkdir /zap/wrk'
                }
            }
        }

        // Etapa para esperar que ZAP esté listo
        stage('Escaneando el target con OWASP ZAP') {
            steps {
                script {
                    scan_type = "${params.SCAN_TYPE}"
                    echo "----> scan_type: $scan_type"
                    target = "${params.TARGET}"
                    if (scan_type == 'Baseline') {
                        bat """
                            docker exec owasp \
                            zap-baseline.py \
                            -t $target \
                            -r report.html \
                            -I
                        """
                    } else if (scan_type == 'APIS') {
                        bat """
                            docker exec owasp \
                            zap-api-scan.py \
                            -t $target \
                            -r report.html \
                            -I
                        """
                    } else if (scan_type == 'Full') {
                        bat """
                            docker exec owasp \
                            zap-full-scan.py \
                            -t $target \
                            -r report.html \
                            -I
                        """
                    } else {
                        echo 'Something went wrong...'
                    }
                }
            }
        }

        stage('Copy Report to Workspace') {
            steps {
                script {
                    bat '''
                        docker cp owasp:/zap/wrk/report.html ${WORKSPACE}/report.html
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Deteniendo y eliminando contenedor del backend'
            bat 'docker-compose down' 
            echo 'Deteniendo y eliminando contenedor del frontend'
            bat 'cd frontend && docker-compose down'
            echo 'Remover OWASP ZAP container'
            bat '''
                docker stop owasp
                docker rm owasp
            '''
            cleanWs()
        }
    }
}
