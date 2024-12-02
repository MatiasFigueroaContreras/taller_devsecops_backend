pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'jdk17'  
    }
    environment {
        SEMGREP_APP_TOKEN = credentials('Semgrep')
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
        stage("Checkout Repositorys") {
            steps {
                script {
                    dir('Backend'){
                        checkout([
                            $class: 'GitSCM', 
                            branches: [[name: '*/pipeline-test-mfc']], 
                            extensions: [], 
                            userRemoteConfigs: [[url: 'https://github.com/MatiasFigueroaContreras/taller_devsecops_backend.git']]
                        ])
                    }
                }
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
                script {
                    dir("Backend"){
                        writeFile file: 'secrets.properties', text: '''admin.password=password
                        admin.correo=correo@gmail.com
                        admin.nombre=nombre
                        admin.apellido_paterno=paterno
                        admin.apellido_materno=materno
                        jwt.secret_key=miClaveSecretaDe32Caracteres0000miClaveSecretaDe32Caracteres0000miClaveSecretaDe32Caracteres0000miClaveSecretaDe32Caracteres0000'''
                    }
                }
                script {
                    dir('frontend') {
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
        stage('Revisión SAST') {
            steps {
                script {
                    dir("Backend"){
                        bat """
                        docker run -e SEMGREP_APP_TOKEN=${env.SEMGREP_APP_TOKEN} --rm -v "%cd%:/src" semgrep/semgrep semgrep ci --json --output /src/semgrep_report.json
                        """
                    }
                }
                archiveArtifacts artifacts: '**/semgrep_report.json', allowEmptyArchive: true
                
                script {
                    dir('frontend'){
                        bat 'npm install'
                    }
                }
                script {
                    dir('frontend'){
                        bat '''
                            npm run lint:html
                        '''
                    }
                }
                archiveArtifacts artifacts: '**/reportes/reporte.html', allowEmptyArchive: true
            }
        }
        stage('Compilación y ejecución de APP') {
            steps {
                script {
                    dir("Backend"){
                        bat 'mvn clean install -DskipTests=true'
                    }
                }
                script {
                    dir('Backend'){
                        bat 'docker-compose up --build -d'
                    }
                    dir('frontend') {
                        bat 'docker-compose up --build -d'
                    }
                }
            }
        }
        stage('Revisión DAST') {
            steps {
                script {
                    bat 'docker pull zaproxy/zap-stable'
                }
                script {
                    scan_type = "${params.SCAN_TYPE}"
                    echo "----> scan_type: $scan_type"
                    target = "${params.TARGET}"
                    if (scan_type == 'Baseline') {
                        bat """
                            docker run --name owasp \
                            -v %cd%:/zap/wrk/:rw \
                            --network="host" \
                            zaproxy/zap-stable \
                            zap-baseline.py \
                            -t $target \
                            -r report.html \
                            -I 
                        """
                    } else if (scan_type == 'APIS') {
                        bat """
                         docker run --name owasp \
                            -v %cd%:/zap/wrk/:rw \
                            --network="host" \
                            zaproxy/zap-stable \
                            zap-api-scan.py \
                            -t $target \
                            -r report.html \
                            -I 
                        """
                    } else if (scan_type == 'Full') {
                        bat """
                         docker run --name owasp \
                            -v %cd%:/zap/wrk/:rw \
                            --network="host" \
                            zaproxy/zap-stable \
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
    }

    post {
        always {
            echo 'Remover OWASP ZAP container'
            bat '''
                docker stop owasp
                docker rm owasp
            '''
        }
    }
}
