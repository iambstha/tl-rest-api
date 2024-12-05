# REST API Project

Welcome to the REST API project. This project provides a set of RESTful endpoints that you can test and integrate into your applications.

## Features
- Implements CRUD operations for managing user, blog and comment resources.
- Includes robust error handling and input validation.
- Designed to be scalable and maintainable.

## Prerequisites
- **Java Development Kit (JDK)**: Version 17 or later
- **Maven**: Version 3.8.0 or later
- **Postman**: To test the APIs

## Getting Started

### 1. Clone the Repository
To get started, clone this repository to your local machine:
```bash
git clone https://github.com/iambstha/tl-rest-api.git
cd tl-rest-api
```

### 2. Build and Run the Project
Use Maven to build and run the project locally:
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

### 3. Test the APIs
The REST API endpoints are available for testing in Postman. You can find all the requests organized in a Postman collection at the following link:

[Postman Collection URL](https://drive.google.com/file/d/1Gt78l5SyPRmzS5z-kW4Sr7RiJxMqtNP2/view?usp=sharing)

#### Steps to Test:
1. Import the Postman collection using the link provided.
2. Make sure your local server is running.
3. Use Postman to send requests to the endpoints and verify the responses.
4. First register a user and get the username, then log in to get the access token that can be used to send requests in various endpoints. 

## Feedback
If you encounter any issues or have suggestions for improvement, feel free to open an issue in this repository or contact me directly.

Thank you for using this REST API project!

