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
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                        def dockerImage = dockerImage.push()
                        dockerImage.tag("cebridani/backend-chess:${env.BUILD_NUMBER}", "cebridani/backend-chess:latest")
                        dockerImage.push("cebridani/backend-chess:${env.BUILD_NUMBER}")
                        dockerImage.push("cebridani/backend-chess:latest")
                    }
                }
            }
        }
    }
}

