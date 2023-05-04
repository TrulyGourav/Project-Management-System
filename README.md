# AProjecto: A Project Management System
This repository contains code for a project named as 'AProjecto' which is a project management system.

NOTE: This project was a part of internship. The complete project is an asset of the organization. Therefore, I only have rights to make backend available publicly (The code I contributed to).

The project consists of two microservices: 

      User microservice : To handle all the API related to user (Admin, Project Manager, User)
      Project microservice : To handle all the API related to project operations
      
There is also a server and an API Gateway service. The Gateway is mainly used to route the APIs to particular microservice without reflecting the port of the         services.


# Technology Stack used for Backend

1. Java
2. Spring Boot
3. Spring Security using JWT
4. Spring Data JPA
5. PostgreSQL as Database

# Supportive Libraries/API used

1. IntelliJ as IDE
2. Gradle: Tool that helped to manage required dependencies in the project
3. Lombok: This library helped in to generate boiler code for most of the cases like getter & setter in POJOs.
4. Postman: To test APIs
5. Hazelcast to implement caching

# Features of AProjecto

1. Register/Login
2. User verification and authentication
3. create task/Sub_task
4. Add user, delete user
5. Default roles + Custom role (on project level)
6. Role wise permissions
7. Adding comments to facilitate communication
8. User Performance
