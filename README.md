# 🍱 Smart Food Rescue & Redistribution System

> A full-stack production-style web application connecting food donors with NGOs and volunteers to reduce food waste.

---

## 🚀 Tech Stack

| Layer      | Technology |
|------------|-----------|
| Backend    | Java 17 + Spring Boot 3.2.5 |
| Security   | Spring Security + JWT (JJWT 0.12.5) |
| Database   | PostgreSQL 15 |
| ORM        | Spring Data JPA (Hibernate) |
| Cache      | Redis 7 |
| API Docs   | Swagger / OpenAPI 3 (springdoc) |
| Frontend   | React 18 + Vite + Axios |
| Container  | Docker + Docker Compose |

---

## 🏗️ Project Structure

```
project/
├── backend/                    # Spring Boot application
│   ├── src/main/java/com/foodrescue/
│   │   ├── config/             # Redis, Security, Swagger, DataInitializer
│   │   ├── controller/         # REST Controllers (9 files)
│   │   ├── dto/                # Request/Response DTOs
│   │   ├── entity/             # JPA entities (9 files)
│   │   ├── enums/              # Enums (7 files)
│   │   ├── exception/          # Custom exceptions + GlobalExceptionHandler
│   │   ├── repository/         # Spring Data repositories (9 files)
│   │   ├── scheduler/          # DonationExpiryScheduler
│   │   ├── security/           # JWT filter, provider, UserDetailsService
│   │   └── service/            # Service interfaces + implementations
│   └── src/main/resources/
│       └── application.yml
├── frontend/                   # React application
│   └── src/
│       ├── api/                # Axios config + API services
│       ├── components/         # Shared components (Navbar)
│       ├── context/            # AuthContext
│       ├── pages/              # Pages for each role
│       │   ├── public/         # Home, Login, Register, About
│       │   ├── donor/          # Dashboard, Create, List, Detail
│       │   ├── ngo/            # Dashboard, Browse, Pickups
│       │   ├── volunteer/      # Dashboard, Tasks
│       │   └── admin/          # Dashboard, Users, NGOs, Complaints, Logs
│       ├── routes/             # Protected/Role route guards
│       └── styles/             # Global CSS
└── docker-compose.yml
```

---

## 🔐 Test Accounts

Created automatically on first startup by `DataInitializer`:

| Role      | Email                       | Password   |
|-----------|-----------------------------|------------|
| Admin     | admin@foodrescue.com        | Admin@123  |
| Donor     | donor@foodrescue.com        | Donor@123  |
| NGO       | ngo@foodrescue.com          | Ngo@12345  |
| Volunteer | volunteer@foodrescue.com    | Vol@12345  |

---

## ▶️ Running Locally (Without Docker)

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 15 (running on port 5432)
- Redis (running on port 6379)
- Node.js 20+

### 1. Create PostgreSQL Database

```sql
CREATE DATABASE food_rescue_db;
```

### 2. Configure Environment (optional)

Default settings in `application.yml` connect to:
- PostgreSQL: `localhost:5432/food_rescue_db` (user: `postgres`, password: `postgres`)
- Redis: `localhost:6379`

Override by setting environment variables:
```powershell
$env:DB_USERNAME = "myuser"
$env:DB_PASSWORD = "mypassword"
```

### 3. Run the Backend

```powershell
cd backend
mvn spring-boot:run
```

Backend starts at **http://localhost:8080**

### 4. Run the Frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend starts at **http://localhost:3000**

---

## 🐳 Running with Docker

```powershell
# Start all services (PostgreSQL + Redis + Backend + Frontend)
docker-compose up --build

# Stop all services
docker-compose down

# Stop and delete all data
docker-compose down -v
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## 📖 API Documentation

After starting the backend, visit:

```
http://localhost:8080/swagger-ui.html
```

Click **Authorize** and paste your JWT token (without `Bearer ` prefix) to test protected endpoints.

---

## 🔄 Key API Endpoints

### Authentication (Public)
```
POST /api/auth/register     # Register (DONOR/NGO/VOLUNTEER)
POST /api/auth/login        # Login → returns JWT
```

### Donations
```
GET  /api/donations                  # Browse all (public)
GET  /api/donations/{id}             # Get single donation
GET  /api/donations/my               # My donations (DONOR)
POST /api/donations                  # Create donation (DONOR)
PUT  /api/donations/{id}             # Update donation (DONOR)
DELETE /api/donations/{id}           # Cancel donation (DONOR)
```

### NGO
```
GET  /api/ngo/available-donations    # Browse available (NGO)
POST /api/ngo/accept-donation/{id}   # Accept donation (NGO)
GET  /api/ngo/my-pickups             # Pickup history (NGO)
```

### Volunteer
```
GET  /api/volunteer/tasks            # Available tasks
POST /api/volunteer/tasks/{id}/accept  # Accept task
POST /api/volunteer/tasks/{id}/collect # Mark collected
POST /api/volunteer/tasks/{id}/deliver # Mark delivered
```

### Admin
```
GET  /api/admin/dashboard            # Platform stats
GET  /api/admin/users                # User management
PUT  /api/admin/ngos/{userId}/approve # Approve NGO
GET  /api/admin/complaints           # View complaints
GET  /api/admin/audit-logs           # Audit trail
```

---

## 🔑 Key Features

- **JWT Authentication** — stateless, role-based access control
- **4 User Roles** — Admin, Donor, NGO, Volunteer
- **Haversine Matching Algorithm** — finds nearest NGOs within 50km
- **Redis Caching** — available donations cached with 10-minute TTL
- **Auto-expiry Scheduler** — expires stale donations every 60 seconds
- **In-app Notifications** — sent at every status change
- **Audit Logging** — every important action is recorded
- **Complaint System** — users can report issues; admins resolve them
- **NGO Approval Gate** — NGOs need admin approval before accepting donations

---

## 🏛️ Architecture

```
Frontend (React) → HTTP/JWT → Controller → Service → Repository → PostgreSQL
                                                 ↓
                                             Redis Cache
                                                 ↓
                                          Notification Service
                                          Audit Log Service
```

### Design Patterns Used
- **Layered Architecture** — Controller → Service Interface → ServiceImpl → Repository
- **DTO Pattern** — entities never exposed directly; DTOs sanitize responses
- **Repository Pattern** — Spring Data JPA abstracts database queries
- **Strategy Pattern** — `MatchingService` interface for swappable algorithms

---

## 🧪 Running Tests

```powershell
cd backend
mvn test
```

Tests use H2 in-memory database (PostgreSQL not needed for tests).

---

## 👨‍💻 Built for Learning

This project demonstrates:
- Spring Boot 3 best practices
- JWT security implementation
- JPA entity relationships (OneToOne, ManyToOne)
- Custom exceptions and global error handling
- Redis caching with `@Cacheable` / `@CacheEvict`
- Scheduled jobs with `@Scheduled`
- Clean React patterns with Context API and Axios
- Docker containerization
