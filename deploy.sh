#!/bin/bash

echo "Deploying Kubernetes configurations..."
kubectl apply -f k8s/pgvector-deployment.yaml
kubectl apply -f k8s/app-deployment.yaml

echo "Checking service status..."
kubectl get pod,deployment,service,secret

echo "Deployment complete! If running locally, use the following command to port-forward and access the application:"
echo "kubectl port-forward service/app-service 8080:8080"
