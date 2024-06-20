# Java Web Server with Spring Framework

This repository contains a Java web server application built using the Spring Framework. The server provides RESTful APIs and serves web content.

## Features

- RESTful API endpoints for CRUD operations
- Web content serving (HTML, CSS, JavaScript)
- Database integration with Spring Data JPA
- Authentication and authorization with Spring Security
- Caching with Spring Cache
- Asynchronous task execution with Spring Task
- Externalized configuration with Spring Environment

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven
- A relational database (e.g., MySQL, PostgreSQL)

## Getting Started

1. Clone the repository:
git clone https://github.com/DragosBrex/Licenta-Server.git
Copy

2. Configure the database connection properties in `src/main/resources/application.properties`.

3. Build the project:
mvn clean install
Copy

4. Run the application:
java -jar target/spring-web-server.jar
Copy
The server will start running on `http://localhost:8080`.

