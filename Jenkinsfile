pipeline {
    agent any
    
    tools {
        gradle 'Gradle 8.6'
        jdk 'JDK 21'
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
