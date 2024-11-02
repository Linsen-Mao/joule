pipeline {
    agent any

    environment {
        IMAGE_NAME = "linsenmao/joule"
        TAG = "v1"
        FULL_IMAGE = "${IMAGE_NAME}:${TAG}"
        DOCKER_CREDENTIALS_ID = 'docker-credentials'
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig'
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([string(credentialsId: GITHUB_TOKEN_CREDENTIALS_ID, variable: 'GITHUB_TOKEN')]) {
                    sh 'git clone https://github.com/Linsen-Mao/joule'
                    sh 'cd joule'
                }
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build(FULL_IMAGE, '--platform linux/amd64 .')
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push ${FULL_IMAGE}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG')]) {
                    sh '''
                    kubectl set image deployment/app app=${FULL_IMAGE} --kubeconfig=$KUBECONFIG
                    kubectl apply -f k8s/pgvector-deployment.yaml --kubeconfig=$KUBECONFIG
                    kubectl apply -f k8s/app-deployment.yaml --kubeconfig=$KUBECONFIG
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed.'
        }
        success {
            echo 'Pipeline succeeded.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
