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

### 2. Build and Run the Project with Docker
Use Docker to build and run the project locally (postgres container and database will be automatically created):
```bash
docker compose build
docker compose up
```
Note: Make sure that the spring profile is set to 'prod' in application.properties file


### 3. Build and Run the Project without Docker
Use Maven to build and run the project locally:
```bash
mvn clean install
mvn spring-boot:run
```
Note: Make sure that the spring profile is set to 'test' in application.properties file. 
Also, postgres instance should be available with the required database setup for a configured user.

The application will start on `http://localhost:8080` by default.

### 3. Test the APIs

#### A. The REST API endpoints are also available for testing in Postman. You can find all the requests organized in a Postman collection at the following link:

[Get Postman Collection](https://drive.google.com/file/d/1XEzJpFu9JGUQlauEE5KDQ6AfXJtjxywU/view?usp=sharing)

#### B. The REST API endpoints are also available for testing in Swagger UI. 
The application swagger UI is available at `http://localhost:8080/swagger-ui/index.html#/` by default.

#### Steps to Test with Postman:
1. Import the Postman collection using the link provided.
2. Make sure your local server is running.
3. Use Postman to send requests to the endpoints and verify the responses.
4. First register a user and get the username, then log in to get the access token that can be used to send requests in various endpoints. 

## Feedback
If you encounter any issues or have suggestions for improvement, feel free to open an issue in this repository or contact me directly.

Thank you for using this REST API project!

