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
                script {
                    sh 'rm -rf joule || true'
                }
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh 'git clone https://${GITHUB_TOKEN}@github.com/Linsen-Mao/joule.git'
                    sh 'cd joule'
                }
            }
        }

        stage('Build with Maven') {
            agent {
                docker { image 'maven:3.9.2-eclipse-temurin-21' }
            }
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            agent {
                docker { image 'docker:24.0.5' }
            }
            steps {
                script {
                    sh "docker build -t ${FULL_IMAGE} --platform linux/amd64 ."
                }
            }
        }

        stage('Push Docker Image') {
            agent {
                docker { image 'docker:24.0.5' }
            }
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
