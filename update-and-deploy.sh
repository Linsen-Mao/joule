#!/bin/bash

IMAGE_NAME="linsenmao/joule"
TAG="v1"
FULL_IMAGE="$IMAGE_NAME:$TAG"

# Build the application package with Maven
echo "Building application with Maven..."
mvn clean package || { echo "Maven build failed"; exit 1; }

# Build the Docker image for the application
echo "Building Docker image with tag $TAG..."
docker build --platform linux/amd64 -t $FULL_IMAGE .

# Login to Docker registry
echo "Logging into Docker Registry..."
docker login || { echo "Docker login failed"; exit 1; }

# Push the Docker image to the registry
echo "Pushing Docker image to registry..."
docker push $FULL_IMAGE || { echo "Docker push failed"; exit 1; }

# Apply Kubernetes configurations
echo "Applying Kubernetes configurations..."
kubectl apply -f k8s/pgvector-deployment.yaml
kubectl apply -f k8s/app-deployment.yaml

# Update the deployment with the new image
echo "Updating Kubernetes deployment with the new image..."
kubectl set image deployment/app app=$FULL_IMAGE

# Verify the status of pods, deployments, and services
echo "Checking the status of Kubernetes resources..."
kubectl get pod,deployment,service