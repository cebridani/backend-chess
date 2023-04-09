pipeline {
   agent any
   stages {
      stage('Build') {
         steps {
            sh 'mvn clean install'
         }
      }
      stage('Docker Build') {
         steps {
            sh 'docker build -t backend-chess .'
         }
      }
      stage('Docker Run') {
         steps {
            sh 'docker run -d -p 3000:3000 --name=backend-chess backend-chess'
         }
      }
   }
}
