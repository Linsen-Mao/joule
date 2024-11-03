# Joule

## Overview

Joule is a digital assistant service that allows users to:

1. **Define a name and response string** for a digital assistant.
2. **Send text messages** to the named assistant and receive predefined responses.
3. **Ask questions** related to uploaded materials and receive intelligent answers.
4. **Upload PDF documents** for the assistant to process, using an event-driven approach.

## Prerequisites

To run this application, make sure you have the following installed and configured on your system:

- **Docker Desktop** with Docker and Docker Compose installed
- **Kubernetes** enabled in Docker Desktop
- An **OpenAI API Key**. Obtain one by signing up on the [OpenAI website](https://openai.com/).

## Setup Instructions

### 1. Configure the OpenAI API Key in Kubernetes

To secure your OpenAI API key, you should store it as a Kubernetes secret and reference it in the deployment file.

Run the following command to create the secret:

```bash
kubectl create secret generic openai-secret --from-literal=OPENAI_API_KEY=<your-actual-openai-api-key>
```

If you want to use Retrieval-Augmented Generation (RAG), the `SPRING_AI_VECTORSTORE_PGVECTOR_ACTIVATED` variable should be set to `true`.
**Note:** Please upload documents first if you choose to use RAG.

```bash
kubectl set env deployment/app SPRING_AI_VECTORSTORE_PGVECTOR_ACTIVATED=true
```

### 2. Deploy the Application

Use the `deploy.sh` script to build, push, and deploy the application to Kubernetes. This script will handle all necessary steps, including starting the database and configuring the application on Kubernetes.

```bash
./deploy.sh
```

### 3. Access the Application

Once deployed, the service will start on **`http://localhost:8080`**. You can verify everything is running by checking the status of the Kubernetes resources:

```bash
kubectl get pods,deployments,services
```

## API Usage

You can interact with the service using HTTP requests via tools like **Postman**.

### 1. Register a Digital Assistant

- **Endpoint:** `POST /api/v1/assistant/register`
- **Description:** Registers a new digital assistant with a specific name, initial response, and optional system prompt.
- **Request Body:**

  ```json
  {
    "name": "Joule",
    "response": "Hello! I’m SAP Joule. How can I assist you today?",
    "systemPrompt": "You are an SAP expert."
  }
  ```

- **Response:**

  ```json
  {
    "status": "success",
    "data": "Assistant Joule registered successfully."
  }
  ```

### 2. Retrieve Assistant Response

- **Endpoint:** `GET /api/v1/assistant/{name}`
- **Description:** Retrieves the stored response string for a specified assistant by name.
- **Path Parameter:**

    - `name` (string): The name of the assistant.

- **Response:**

    - **Success:**

      ```json
      {
        "status": "success",
        "data": "Hello! I’m SAP Joule. How can I assist you today?"
      }
      ```

    - **Error (Assistant not found):**

      ```json
      {
       "status": "error",
       "message": "No assistant found with name: Joule1"
      }
      ```

### 3. Upload a PDF Document for Assistant Processing

- **Endpoint:** `POST /api/v1/assistant/{name}/upload`
- **Description:** Uploads a PDF document for a specific assistant. The document will be processed asynchronously using an event-driven approach.
- **Path Parameter:**

    - `name` (string): The name of the assistant.

- **Form Data Parameter:**

    - `file` (MultipartFile): The PDF file to be uploaded.

- **Response:**

    - **Success:**

      ```json
      {
        "status": "success",
        "message": "PDF upload event published for assistant: Joule"
      }
      ```

### 4. Ask a Question to the Assistant

- **Endpoint:** `POST /api/v1/assistant/{name}/answer`
- **Description:** Sends a question to the specified assistant and retrieves an answer.
- **Path Parameter:**

    - `name` (string): The name of the assistant.

- **Request Body:**

  ```json
  {
    "question": "explain document chain tracing in 2 sentences"
  }
  ```

- **Response:**

    - **Success:**

      ```json
      {
        "status": "success",
        "data": "Document chain tracing in SAP involves the Flow Builder following a sequence of steps to trace back and clear documents in a series of steps, starting from the current documents and moving through clearing documents in successive steps. This process helps in analyzing accounting scenarios such as accounts payable and receivable, payments, and bank statement postings, providing insights into liquidity items and cash flows."
      }
      ```

    - **Error (Answer not found):**

      ```json
      {
        "status": "error",
        "message": "No answer found for the given question"
      }
      ```