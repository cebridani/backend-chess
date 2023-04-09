pipeline {
    agent any
    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
    }
    stages {
        stage('Build') {
            steps {
                dir('chess-web-api') {
                    bat 'mvn clean install'
                }
            }
        }
        stage('Docker Build') {
            steps {
                dir('chess-web-api') {
                    bat 'docker build -t backend-chess .'
                }
            }
        }
        stage('Docker Run') {
            steps {
                bat 'docker run -d -p 3000:3000 --name=backend-chess backend-chess'
            }
        }
    }
}

