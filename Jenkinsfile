pipeline {
    agent any

    environment {
        IMAGE_NAME = "linsenmao/joule"
        TAG = "v1"
        FULL_IMAGE = "${IMAGE_NAME}:${TAG}"
        DOCKER_CREDENTIALS_ID = 'docker-credentials'
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig'
        OPENAI_API_KEY_CREDENTIALS_ID = 'OPENAI_API_KEY'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    sh 'rm -rf joule || true'
                }
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh 'git clone --depth=1 https://${GITHUB_TOKEN}@github.com/Linsen-Mao/joule.git'
                    sh 'cd joule'
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${FULL_IMAGE} --platform linux/amd64 ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                        sh "docker push ${FULL_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG'),
                                     string(credentialsId: OPENAI_API_KEY_CREDENTIALS_ID, variable: 'API_KEY')]) {
                        sh 'kubectl create secret generic openai-secret --from-literal=OPENAI_API_KEY="${API_KEY}" --dry-run=client -o yaml | kubectl apply -f -'
                        sh 'kubectl set image deployment/app app=${FULL_IMAGE} --kubeconfig=$KUBECONFIG'
                        sh 'kubectl apply -f k8s/pgvector-deployment.yaml --kubeconfig=$KUBECONFIG'
                        sh 'kubectl apply -f k8s/app-deployment.yaml --kubeconfig=$KUBECONFIG'
                    }
                }
            }
        }

        stage('Debug Environment Variables') {
            steps {
                script {
                    echo "API_KEY from Jenkins: ${API_KEY}"
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
