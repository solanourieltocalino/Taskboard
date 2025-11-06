# 🧩 TaskBoard – Java Backend API

**TaskBoard** is a backend application built with **Spring Boot 3** and **MySQL 8**, designed as a **portfolio project** to demonstrate backend development skills with real-world structure, testing, and documentation.

The API manages **Users**, **Projects**, and **Tasks** with full CRUD operations, validations, pagination, exception handling, and layered architecture.

---

## 🧱 Tech Stack

- **Java 17 (Temurin)**
- **Spring Boot 3.5.x**
  - Spring Web
  - Spring Validation
  - Spring Data JPA
  - Flyway (database migrations)
- **MySQL 8** (via Docker container)
- **Maven 3.9+**
- **JUnit 5 + Mockito + Jacoco**
- **Git + GitHub** (main / develop / feature flow)

---

## ⚙️ Quick Setup

### Prerequisites

- Java 17  
- Maven 3.9+  
- Docker & Docker Compose

### Database

MySQL service runs in Docker (configured for the `taskboard` schema):

```bash
docker compose up -d
```

---

### 🗄️ Development Database Settings

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskboard?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=admin
spring.datasource.password=admin
```

---

### ▶️ Running the Application

```bash
mvn clean package
mvn spring-boot:run
```

The app starts at: http://localhost:8080

---

### 🧪 Testing

Unit tests are implemented using **JUnit 5** and **Mockito**, covering service and exception layers.  
Code coverage reports are generated via **Jacoco**.

#### 🧷 Run all tests
```bash
mvn test
```

#### 🧷 Run a single test class
```bash
mvn -Dtest=AppUserServiceImplTest test
```

---

### 🧩 API Structure

| **Resource** | **Methods** | **Description** |
|---------------|-------------|-----------------|
| `/users` | `GET`, `POST`, `PUT`, `DELETE` | CRUD for application users |
| `/projects` | `GET`, `POST`, `PUT`, `DELETE` | CRUD for projects linked to users |
| `/tasks` | `GET`, `POST`, `PUT`, `DELETE` | CRUD for tasks with filters by status, priority, and project |

---

### 📬 Postman Collection

A complete **Postman collection** and **environment** are included under:
```bash
docs/postman/Taskboard.postman_collection.json
docs/postman/Taskboard.postman_environment.json
```
Import them in Postman and set the baseURL variable to your local or deployed API.

---

### 🧱 Git Branch Workflow

| **Branch** | **Purpose** |
|-------------|-------------|
| **main** | Stable production branch |
| **develop** | Integration/testing branch |
| **feature/*** | Feature or fix branches |

---

#### 🧩 Example workflow

```bash
git checkout develop
git pull
git checkout -b feature/tasks-crud
# make changes and commits
git push -u origin feature/tasks-crud
```

Then create a Pull Request → develop on GitHub.

---

## ✅ DoD (Definition of Done)
- Project builds and runs without errors
- All tests pass successfully
- Validation and error messages are clear  
- Clean code, no warnings
- Updated README and Postman collection

---

## 👤 Author

#### Solano Tocalino
- LinkedIn: https://www.linkedin.com/in/solanotocalino
- GitHub: https://github.com/solanourieltocalino

---

## 📄 License
Personal educational project — free for learning and reference purposes.
