pipeline {
    agent any
    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
    }
    stages {
        
        stage('Build') {
            steps {
                dir('chess-web-api') {
                    bat 'mvn clean install -DskipTests'
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
                bat 'docker rm -f backend-chess || true'
                bat 'docker run -d -p 3000:3000 --name=backend-chess backend-chess'
            }
        }
        
        stage('Docker Pull') {
          steps {
            script {
              withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                bat 'docker login -u %USERNAME% -p %PASSWORD% && docker pull cebridani/backend-chess:latest'
              }
            }
          }
        }
    }
}

