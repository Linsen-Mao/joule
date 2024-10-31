# Joule

## Overview

Joule is a digital assistant service that allows users to:

1. **Define a name and response string** for a digital assistant.
2. **Send text messages** to the named assistant and receive the predefined responses.
3. **Ask questions related to SAP Finance** and receive intelligent answers.

## Prerequisites

- **Docker** installed on your system.
- An **OpenAI API Key**. You can obtain one by signing up on the [OpenAI website](https://openai.com/).

## Setup Instructions

### 1. Start the Database

Use the provided `compose.yaml` file to start the database using Docker Compose:

```bash
docker-compose -f compose.yaml up -d
```

### 2. Configure the Application

Create an environment variable for your OpenAI API Key:



  ```bash
  export OPENAI_API_KEY=your-openai-api-key
  ```


Alternatively, you can add the API key to the application's `application.properties` file:

```properties
openai.api.key=your-openai-api-key
```

If you want to use RAG, set the attribute to be true.

```properties
spring.ai.vectorstore.pgvector.activated=true
```

If it is the first time you upload the PDFs, set this attribute to be true to embed documents.

```properties
spring.ai.vectorstore.pgvector.reingest-on-start=true
```

Then upload the **PDF** files under the **resources/docs** folder

### 3. Build and Run the Application

The service will start on **`http://localhost:8080`**.

---

## API Usage

You can interact with the service using HTTP requests via tools like **Postman**.

### 1. Register a Digital Assistant

- **Endpoint:** `POST /api/v1/assistant/register`
- **Description:** Registers a new digital assistant with a specific name and initial response.
- **Request Body:**

  ```json
  {
    "name": "Joule",
    "response": "Hello! I’m SAP Joule. How can I assist you today?"
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
        "message": "No response found for assistant: Joule1"
      }
      ```

### 3. Ask a Question to the Assistant

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
