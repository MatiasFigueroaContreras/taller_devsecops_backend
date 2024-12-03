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
            description: 'Tipo de escaneo que se va a realizar dentro del contenedor',
            name: 'SCAN_TYPE'
            
        string defaultValue: 'http://localhost:3000',
            description: 'URL de la aplicacion a escanear',
            name: 'TARGET'
    }

    stages {
        stage("Obtener repositorios") {
            steps {
                script {
                    dir('backend'){
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
                    dir("backend"){
                        writeFile file: 'secrets.properties', text: '''
                        admin.password=adminpass1234
                        admin.correo=admin@milkstgo.cl
                        admin.nombre=admin
                        admin.apellido_paterno=paterno
                        admin.apellido_materno=materno
                        jwt.secret_key=4D6251655468576D5A7134743777217A24432646294A404E635266556A586E32
                        '''
                    }
                }
                script {
                    dir('frontend') {
                        writeFile file: '.env', 
                        text: '''
                        NEXT_PUBLIC_API_URL=http://host.docker.internal:8090
                        NEXTAUTH_SECRET=kDIoxobrY2ut97pwem58BNzVMxAhHXzI96A2vNLlM78=
                        NEXTAUTH_URL=http://localhost:3000
                        '''
                    }
                }
            }
            
        }
        stage('Ejecucion analisis SAST') {
            steps {
                script {
                    dir("backend"){
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
        stage('Compilacion y ejecucion de APP') {
            steps {
                script {
                    dir("backend"){
                        bat 'mvn clean install -DskipTests=true'
                    }
                }
                script {
                    dir('backend'){
                        bat 'docker-compose up --build -d'
                    }
                    dir('frontend') {
                        bat 'docker-compose up --build -d'
                    }
                }
            }
        }
        stage('Ejecucion analisis DAST') {
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
                            -r dast_report.html \
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
                            -r dast_report.html \
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
                            -r dast_report.html \
                            -I 
                        """
                    } else {
                        echo 'Something went wrong...'
                    }
                }
                script {
                    archiveArtifacts artifacts: 'dast_report.html', allowEmptyArchive: true
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
        success {
            script {
                def date = new Date()
                def formattedDate = date.format('yyyy-MM-dd HH:mm:ss')
                emailext(
                    to: 'ignacio.moreira@usach.cl, gaspar.catalan@usach.cl, matias.figueroa.c@usach.cl, cristobal.marchant@usach.cl',
                    from: 'ignaciomoreiracarvach@gmail.com,',
                    subject: "Pipeline de Jenkins: ¡La ejecución terminó exitosamente! (${currentBuild.fullDisplayName})",
                    body: """El pipeline de Jenkins ${currentBuild.fullDisplayName} terminó su ejecución sin fallos.
Detalles: 
- Estado: Exitoso 
- Nombre del pipeline: ${currentBuild.fullDisplayName}
- Fecha de ejecución: ${formattedDate}

Puedes ver los detalles del job en el siguiente enlace: 
${BUILD_URL}
                
Logs completos: ${BUILD_URL}console"""
            )
            }
        }
        
       failure {
            script {
                def date = new Date()
                def formattedDate = date.format('yyyy-MM-dd HH:mm:ss')
                emailext(
                    to: 'ignacio.moreira@usach.cl, gaspar.catalan@usach.cl, matias.figueroa.c@usach.cl, cristobal.marchant@usach.cl',
                    from: 'mteam@gmail.com',
                    subject: "Pipeline de Jenkins: ¡Fallo en la ejecución! (${currentBuild.fullDisplayName})",
                    body: """El pipeline de Jenkins ${currentBuild.fullDisplayName} falló durante su ejecución.
Detalles: 
- Estado: Fallido 
- Nombre del pipeline: ${currentBuild.fullDisplayName}
- Fecha de ejecución: ${formattedDate}

Puedes ver los detalles del job en el siguiente enlace: 
${BUILD_URL}
                
Logs completos: ${BUILD_URL}console"""
            )
            }
        }
    }
}
