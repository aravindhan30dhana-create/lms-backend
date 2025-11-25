# LMS Backend

This repository contains the backend for the Learning Management System (LMS). It is built using **Spring Boot**, **MySQL**, and provides REST APIs for managing users, courses, authentication, and more.

---

## 🚀 Features

* User Signup & Login (JWT Authentication)
* Role-based Access (Admin / Student / Instructor)
* Course Management
* Student Enrollment
* Secure Password Hashing

---

## 📂 Project Structure

```
src/
 ├── main/
 │   ├── java/com/...   # Java source code
 │   └── resources/     # application.properties, static files
 └── test/              # Unit tests
```

---

## 🛠️ Technologies Used

* **Spring Boot**
* **Spring Security + JWT**
* **MySQL Database**
* **Hibernate / JPA**
* **Maven**

---

## ⚙️ Configuration

### Update MySQL connection in `application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/lmsdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### JWT Secret Key

```
app.jwtSecret=your_secret_key_here
app.jwtExpirationMs=86400000
```

---

## ▶️ Running the Project

### 1. Clone the repository

```
git clone https://github.com/aravindhan30dhana-create/lms-backend.git
cd lms-backend
```

### 2. Build the project

```
mvn clean install
```

### 3. Run the server

```
mvn spring-boot:run
```

Server will start at:

```
https://lms-backend-production-c5d8.up.railway.app
```

---

## 🧪 API Testing

Use **Postman**, **Thunder Client**, or your frontend to test the APIs.

### 🌐 Live Backend URL

```
https://lms-backend-production-c5d8.up.railway.app
```

### Example Endpoints

* `POST /api/auth/signup`
* `POST /api/auth/signin`
* `GET /api/courses` → Live: [https://lms-backend-production-c5d8.up.railway.app/api/courses](https://lms-backend-production-c5d8.up.railway.app/api/courses)

---

## 📜 License

This project is open-source and available under the MIT License.

---

If you want, I can also create a **README for the frontend**, or add diagrams, API tables, screenshots, or badges.
