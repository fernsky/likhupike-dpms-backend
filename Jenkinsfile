pipeline {
    agent any
    
    environment {
        JAVA_HOME = '/var/jenkins_home/tools/hudson.model.JDK/JDK_21/jdk-21.0.5'
        PATH = "${JAVA_HOME}/bin:${PATH}"
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
    }
    
    post {
        always {
            junit '**/build/test-results/test/*.xml'
            cleanWs()
        }
    }
}
