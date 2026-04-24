# Technical Test: Franchises API

This project is a RESTful API built with **Spring Boot WebFlux** utilizing a **Reactive Clean Architecture**. It was generated and structured using the official **[Bancolombia Clean Architecture Scaffold](https://github.com/bancolombia/scaffold-clean-architecture)** as a foundation. It manages Franchises, Branches, and Products, including features for tracking and updating product stock levels across multiple branches.

## 🛠️ Technologies Used
- **Java 25**
- **Spring Boot 3 WebFlux** (Reactive programming)
- **Spring Data R2DBC** (Non-blocking database access)
- **PostgreSQL**
- **Docker & Docker Compose** (Containerized database environment)
- **Gradle** (Build tool)
- **Swagger / OpenAPI** (API Documentation)

---

## 🌍 Live Deployment
The application has been deployed to the cloud for easy testing and evaluation:
- **Backend API (Render):** [https://back-franchises.onrender.com/](https://back-franchises.onrender.com/)
- **Database (Supabase):** Remote PostgreSQL instance.
- **Cloud Swagger UI:** [https://back-franchises.onrender.com/swagger-ui.html](https://back-franchises.onrender.com/swagger-ui.html)

---

## ⚙️ Prerequisites
To run this application locally, you will need:
- **Docker** and **Docker Compose** installed and running on your machine.
- **Java 25** (JDK) installed.

---

## 🚀 Local Execution Instructions

### Option A: Run Everything with Docker Compose (Recommended)
The project includes a `docker-compose.yml` file configured to build and spin up **both** the PostgreSQL database and the Spring Boot application together in one command.

Open a terminal at the root of the project and run:
```bash
docker-compose up --build
```
This will start both containers and show the logs in your terminal. Wait until you see that the Spring Boot application has started. The API will be available at `http://localhost:8080`.

*Note: The database container uses an `init.sql` script to automatically generate the required database schema upon creation.*

### Option B: Run the Application with Gradle
If you prefer to run the application locally (e.g., for debugging), you can start just the database with Docker, and run the app using Gradle.

1. Start only the database (in detached mode so you can keep using the terminal):
```bash
docker-compose up -d db
```
2. Run the application:
```bash
./gradlew bootRun
```
The application will start on `http://localhost:8080` and automatically connect to the local PostgreSQL instance.

### 3. Running the Tests (Optional)
To compile the project and run the comprehensive suite of unit tests:
```bash
./gradlew build
```

---

## 📖 API Documentation (Swagger)
The API is fully documented using OpenAPI. Once the application is running, you can access the interactive Swagger UI to test the endpoints:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Docs:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Summary of Available Endpoints
All endpoints are cleanly grouped under the `/api` prefix:

**Franchises:**
- `GET /api/franchises` - Retrieve all franchises.
- `POST /api/franchises` - Create a new franchise.
- `PATCH /api/franchises/{idFranchise}` - Update a franchise name.
- `GET /api/franchises/{idFranchise}/products/top-stock-by-branch` - Get the product with the highest stock for each branch of a given franchise.

**Branches:**
- `GET /api/branches` - Retrieve branches (supports optional `?idFranchise=` query filter).
- `POST /api/franchises/{idFranchise}/branches` - Create a branch for a specific franchise.
- `PATCH /api/branches/{idBranch}` - Update a branch name.

**Products:**
- `GET /api/products` - Retrieve products (supports optional `?idBranch=` query filter).
- `POST /api/branches/{idBranch}/products` - Create a product for a specific branch.
- `PATCH /api/products/{idProduct}` - Update a product (name, stock, or both).
- `DELETE /api/products/{idProduct}` - Delete a product.

---

## 🏗️ Architecture & Design Decisions
This application was designed using **Clean Architecture** principles, generated via the **Bancolombia Clean Architecture Scaffold** plugin (`co.com.bancolombia.cleanArchitecture`).

- **Domain-Driven Design:** The core business rules (`Franchise`, `Branch`, `Product` entities and UseCases) are isolated in the `domain` module. The domain has **zero dependencies** on external frameworks (like Spring).
- **Reactive Stack:** Uses Project Reactor (`Mono`/`Flux`) from end to end. This ensures non-blocking I/O operations and high concurrency performance, paired with the reactive R2DBC database driver.
- **Infrastructure Layer:** Contains driven adapters (R2DBC PostgreSQL integration) and entry points (Reactive Web handlers and routers).
- **Global Error Handling:** Custom exceptions (`BusinessException`) are thrown from the domain and mapped into standardized HTTP responses using a global `WebExceptionHandler`.
