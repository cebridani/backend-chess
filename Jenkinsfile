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
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    env.KUBE_NAMESPACE = "chess"
                    env.KUBECONFIG = "C:\\Users\\danie\\.kube\\config"
                }
                bat '''
                    kubectl config use-context rancher-desktop
                    kubectl config set-context --current --namespace=%KUBE_NAMESPACE%
                    kubectl apply -f deplyment.yaml
                '''
            }
        }
    }
}

