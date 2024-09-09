# Classroom Spring Boot Project

This project aims to facilitate communication between teachers and students. It provides a platform where both students and teachers can log in to their respective accounts. Teachers can create multiple courses, and students can enroll in various courses. Additionally, teachers can suggest books related to the courses. The platform enhances interaction and communication between teachers and students.

---

### Key Features:

- **General Features:**
  - Different login and registration processes for teachers and students.
  - Separate dashboards for both teachers and students after login.
  
- **Teacher Features:**
  - Create and manage multiple courses.
  - Suggest relevant books and materials for each course.
  
- **Student Features:**
  - Enroll in multiple courses offered by different teachers.
  - Access suggested books and resources.
---

### Versions
- **Spring Boot**: 3.3.3
- **Java**: 17.0
- **Packaging**: Jar

---

### Dependencies
- **Spring Data JPA**: Provides a simplified and more efficient way to interact with relational databases.
- **MySQL Driver**: Allows your application to communicate with a MySQL database.
- **Spring Web**: Supports REST APIs, follows MVC architecture, and handles HTTP requests.
- **Spring Boot DevTools**: Provides features like automatic restart, live reload, and faster feedback.
- **Spring Security**: A highly customizable authentication and access-control framework for Spring applications.
- **Lombok**: Reduces boilerplate code.
- **Thymeleaf**: Template engine for server-side rendering.

---

## How to Run the Project

### Prerequisites:
1. **Java 17**
2. **Maven**
3. **MySQL**

### Steps to Run the Project:
1. Clone the Repository: Clone the project from the GitHub repository to your local machine:
```bash
    git clone https://github.com/Boorhan/Classroom-Spring-Boot-Project.git
```

2. Configure the Database: Make sure your MySQL server is running and update the database settings in the application.yml file:

```bash
spring:
  application:
    name: Classroom
  datasource:
    url: jdbc:mysql://localhost:3306/Classroom
    username: root
    password: root
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update

```
3. Build the Project: Use Maven to build the project.

4. Run the Application: Have Fun!
```bash
    http://localhost:8080
```