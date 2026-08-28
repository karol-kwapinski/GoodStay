# GoodStay

A web-based hotel booking platform.
Users can search, browse, and reserve hotel rooms across multiple properties.

---

## About the project

This application is a hotel reservation system that allows:
- searching hotels by location and filters
- viewing available rooms
- making and managing reservations
- admin panel for managing hotels and rooms

The system is built using Spring and follows a layered architecture (Controller – Service – Repository).

---

## Tech Stack

### Backend
- Java 25
- Spring Framework
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Tomcat

### Frontend
- React

### Testing
- JUnit 5
- Mockito
- Spring Test - integration tests

---

## Features

- Hotel search and listing
- Room availability checking
- Reservation system
- User accounts
- Admin management panel
- REST API support

---

## Requirements

- Java 25
- Maven
- Apache Tomcat 11
- Docker & Docker Compose
- Node.js and npm

---

## How to run

### 1. Clone the repository

```bash
git clone https://github.com/karol-kwapinski/GoodStay.git
cd GoodStay
```

### 2. Start the database

```bash
docker compose up -d
```

For integration tests start the test database:

```bash
docker compose -f docker-compose-test.yml up -d
```

### 3. Build the backend

```bash
mvn clean package
```

Deploy the generated `.war` file to Apache Tomcat and start the server.

Alternatively, you can run the project using the configured Tomcat run configuration in IntelliJ IDEA.

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

## Default ports

- Backend: http://localhost:8082
- Frontend: http://localhost:5173

## Author

Karol Kwapiński
