pipeline {
    agent any
    
    environment {
        JAVA_HOME = '/var/jenkins_home/tools/hudson.model.JDK/JDK_21/jdk-21.0.5'
        PATH = "${JAVA_HOME}/bin:${PATH}"
        DOCKER_IMAGE = 'isresearch/dpms-api'
        DOCKER_CREDENTIALS = credentials('docker-hub-credentials')
    }
    
    tools {
        gradle 'Gradle 8.13'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'gradle clean build -x test'
            }
        }
        
        stage('Test') {
            steps {
                sh 'gradle test'
            }
        }
        
        stage('Docker Build') {
            steps {
                sh """
                    docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .
                    docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest
                """
            }
        }
        
        stage('Docker Push') {
            steps {
                sh """
                    echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                    docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                    docker push ${DOCKER_IMAGE}:latest
                """
            }
        }
        
        stage('Deploy') {
            steps {
                sh """
                    docker-compose -f docker-compose.prod.yaml pull
                    docker-compose -f docker-compose.prod.yaml up -d
                """
            }
        }
    }
    
    post {
        always {
            junit '**/build/test-results/test/*.xml'
            cleanWs()
        }
    }
}
