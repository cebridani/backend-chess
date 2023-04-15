pipeline {
    agent any
    environment {
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
        dockerImage = "cebridani/backend-chess:latest"
    }
    stages {
        
        stage('Build') {
            steps {
                dir('chess-web-api') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                dir('chess-web-api') {
                    sh "docker build -t $dockerImage ."
                }
            }
        }
        
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                }
                sh "docker push $dockerImage"
            }
        }
        
        stage('Deploy') {
            steps {
                sh "kubectl config use-context minikube --kubeconfig=/var/lib/jenkins/.kube/config"
                sh "kubectl config set-context minikube --namespace=chess --kubeconfig=/var/lib/jenkins/.kube/config"
                sh "kubectl rollout restart deployment/backend-chess --kubeconfig=/var/lib/jenkins/.kube/config"
                sh "kubectl rollout status deployment/backend-chess --kubeconfig=/var/lib/jenkins/.kube/config"
            }
        }
    }
}
