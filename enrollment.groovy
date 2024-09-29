pipeline {
    agent any 
    environment {
    DOCKERHUB_CREDENTIALS = credentials('docker_registry')
    }
    stages { 
        stage('SCM Checkout') {
            steps{
            git branch: 'development', url: 'https://github.com/Vikx001/Studio-Ghibli.git'
            }
        }

        //stage('Build docker image') {
            //steps {  
                //sh 'cd /var/lib/jenkins/workspace/cour/backend/courses/Dockerfiles &&  docker build -t studioghibli06/courses:$BUILD_NUMBER .'
                 //sh 'cd /var/lib/jenkins/workspace/cour/backend/courses/Dockerfiles &&  docker build -f Dockerfile -t studioghibli06/courses:$BUILD_NUMBER ../courses'
            //}
        //}
        stage('Build and Test') {
            steps {
                script {
                    //def folders = ['admin_frontend', 'frontend/studio-ghibli', 'backend/gateway', 'backend/users', 'backend/customers', 'backend/courses', 'backend/enrollment']
                    def folders = ['backend/enrollment']
                    for (folder in folders) {
                        // Modify folder name to replace / with -
                        def repositoryName = folder.replaceAll('/', '-')
                        
                        // Build Docker image
                        sh "docker build -f ./${folder}/Dockerfiles/Dockerfile -t studioghibli06/${repositoryName}:$BUILD_NUMBER ./${folder}"
                        // Run SonarQube analysis
                        //sh "sonar-scanner -Dsonar.projectKey=${repositoryName} -Dsonar.sources=./${folder}"
                    }
                }
            }
        }        
        stage('login to dockerhub') {
            steps{
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }
        stage('push image') {
            steps{
                sh 'docker push studioghibli06/backend-enrollment:$BUILD_NUMBER'
            }
        }
}
post {
        always {
            sh 'docker logout'
        }
    }
}