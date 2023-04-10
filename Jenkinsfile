pipeline {
    agent any
    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
        dockerImage = "cebridani/backend-chess:latest"
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
                    bat "docker build -t $dockerImage ."
                }
            }
        }
        
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                }
                bat "docker push $dockerImage"
            }
        }
        
        stage('Deploy') {
            steps {
                bat "kubectl config use-context minikube --kubeconfig=C:\\Users\\danie\\.kube\\config"
                bat "kubectl config set-context minikube --namespace=chess --kubeconfig=C:\\Users\\danie\\.kube\\config"
                bat "kubectl rollout restart deployment/backend-chess --kubeconfig=C:\\Users\\danie\\.kube\\config"
                bat "kubectl rollout status deployment/backend-chess --kubeconfig=C:\\Users\\danie\\.kube\\config"
            }
        }
        
        stage('Clean up') {
            steps {
                bat 'docker stop backend-chess || true'
                bat 'docker rm backend-chess || true'
            }
        }
    }
}
