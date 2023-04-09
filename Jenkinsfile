pipeline {
    agent any
    environment {
        JAVA_HOME = "C:\\Program Files\\Java\\jdk-17"
        dockerImage = "cebridani/backend-chess"
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
                    bat 'docker build -t $dockerImage .'
                }
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
                    bat 'docker push $dockerImage'
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    def app = 'backend-chess'
                    def image = dockerImage
                    def container = "chess-backend"
                    def label = "app=${app}"
                    def selector = "app=${app}"
                    def port = "3000"
                    
                    sh "kubectl set image deployment/${container} ${container}=${image}:${BUILD_NUMBER} --record=true"
                    
                    timeout(time: 2, unit: 'MINUTES') {
                        sh "kubectl rollout status deployment/${container}"
                    }
                    
                    sh "kubectl patch deployment ${container} -p '{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"${label}\": \"${BUILD_NUMBER}\"}}}}}'"
                    
                    sh "kubectl expose deployment/${container} --port=${port} --type=NodePort --labels=${label} --name=${container}-service"
                }
            }
        }
    }
}
