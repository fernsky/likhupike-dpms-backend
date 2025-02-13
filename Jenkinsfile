pipeline {
    agent any
    
    tools {
        gradle 'Gradle 8.13'
        jdk 'JDK 17'
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
