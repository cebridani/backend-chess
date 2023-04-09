pipeline {
   agent any
   stages {
      stage('Build') {
         steps {
            bat 'mvn clean install'
         }
      }
      stage('Docker Build') {
         steps {
            bat 'docker build -t backend-chess .'
         }
      }
      stage('Docker Run') {
         steps {
            bat 'docker run -d -p 3000:3000 --name=backend-chess backend-chess'
         }
      }
   }
}
