# 🚌 BusApp — Bus Ticket Management System

A console-based Java application for managing bus trips and tickets, featuring authentication, trip booking, cancellation with credit balance, and real-world distance calculation via the OSRM routing API.

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Known Issues & Roadmap](#known-issues--roadmap)

---

## About the Project

BusApp is a backend-focused Java project built to practice JPA/Hibernate, the DAO pattern, entity relationships, transaction management, and external API integration. It simulates a real-world bus ticketing platform where two types of users interact:

- **Travelers** — can browse available trips, buy tickets, and cancel with credit refund
- **Bus Companies** — can create and manage trips between Brazilian cities, with real-route pricing

---

## Features

### Traveler
- Sign up / login with email and password
- Buy tickets for available trips (can buy for another person)
- View purchased tickets, with optional filter for canceled ones
- Cancel tickets up to 1 hour before departure (with automatic credit refund)
- Update profile (name, CPF, birth date, telephone, password) or delete account

### Bus Company
- Sign up / login with email and password
- Create trips between cities with auto-suggested price based on **real driving distance** (via OSRM)
- View and manage existing trips (edit origin, destination, departure time, price)
- Delete trips — blocked automatically if the trip already has tickets sold
- Update company profile (legal name, trading name, CNPJ, telephone, password)

### System
- Real route distance calculation via the **OSRM (Open Source Routing Machine)** API, with automatic fallback to the **Haversine formula** if the API is unavailable
- Automatic trip category classification: `INTERCITY` or `INTERSTATE`
- Automatic code generation for trips and tickets
- Per-operation transaction handling (no long-lived transactions wrapping menu loops)
- Brazilian city and state data with UF/name lookup
- Credentials loaded from **environment variables** (no hardcoded secrets)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21+ |
| ORM | Hibernate / Jakarta Persistence (JPA) |
| Database | MySQL |
| HTTP Client | Java 11+ native `HttpClient` |
| JSON Mapping | Jackson (`jackson-databind`) |
| External API | OSRM (routing/distance) |
| Build | Maven |
| Utilities | Lombok, FlatLaf (Swing UI prototype) |

---

## Project Structure

```
src/main/java/br/com/javamastery/
│
├── bytebank/                # Entry points / screens (UI layer)
│   ├── MainScreen.java          # Traveler flow
│   ├── BusCompanyMainScreen.java   # Bus company flow
│   └── AddressMainScreen.java   # Address/city search
│
├── client/                  # External API integration
│   ├── OsrmClient.java          # OSRM HTTP client
│   └── dto/
│       └── OsrmResponse.java    # OSRM JSON response mapping
│
├── dao/                     # Data Access Layer
│   ├── AddressDAO.java
│   ├── BusCompanyDAO.java
│   ├── BusTicketDAO.java
│   ├── EmailDAO.java
│   ├── TravelerDAO.java
│   └── TripDAO.java
│
├── exception/                # Custom domain exceptions (planned/in progress)
│   ├── TripNotFoundException.java
│   ├── TicketNotFoundException.java
│   ├── CityNotFoundException.java
│   ├── InvalidPriceException.java
│   ├── InvalidCredentialsException.java
│   ├── EmailAlreadyExistsException.java
│   ├── CancellationDeadlineExceededException.java
│   └── TripAlreadySoldException.java
│
├── models/                  # JPA Entities
│   ├── BusCompany.java
│   ├── BusTicket.java
│   ├── Category.java (enum)
│   ├── City.java
│   ├── Email.java
│   ├── EmailType.java (enum)
│   ├── State.java
│   ├── Traveler.java
│   └── Trip.java
│
└── util/                    # Utilities
    ├── JPAUtils.java            # EntityManagerFactory singleton
    └── ValidationUtils.java    # Code generation
```

> **Note:** A `service/` layer (`TripService`, `TicketService`, `AuthService`, `DistanceService`) is currently being introduced to separate business logic from the UI layer. See [Roadmap](#known-issues--roadmap).

---

## Database Schema

```
states          cities          traveler        email
─────────       ──────────      ────────────    ──────────
state_code PK   IBGE_code PK    id PK           id PK
uf              city            name            email
name            latitude        birth_date      password
region          longitude       cpf              email_type
                ddd             telephone
                state_code FK   credits_balance
                                email_id FK
                                created_at
                                edited_at

bus_company     trips            bus_ticket
───────────     ──────────       ──────────────
id PK           id PK            id PK
legal_name      code             code
trading_name    origin_city FK   traveler_id FK
cnpj            destination_city FK  trip_id FK
email_id FK     price            price
telephone       company_id FK    departure_date
created_at      departure_time   sale_date
edited_at       distance_km      canceled
                category         cancel_date
                active           edited_at
                created_at       buyer_id
                edited_at
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8+

### 1. Clone the repository

```bash
git clone https://github.com/Emanoel-H/Java-Mastery.git
cd Java-Mastery
```

### 2. Set up the database

Create a MySQL database and import your schema. Make sure the tables match the entities in `models/`.

### 3. Configure environment variables

See [Environment Variables](#environment-variables) section below.

### 4. Build and run

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="br.com.javamastery.bytebank.MainScreen"
```

> Run `BusCompanyMainScreen` instead if you want to access the bus company flow.

---

## Environment Variables

The application reads database credentials from environment variables to avoid hardcoding secrets.

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/busapp` |
| `DB_USER` | Database username | `root` |
| `DB_PASS` | Database password | `yourpassword` |

**On Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/busapp"
$env:DB_USER="root"
$env:DB_PASS="yourpassword"
```

**On Linux/macOS:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/busapp"
export DB_USER="root"
export DB_PASS="yourpassword"
```

---

## Known Issues & Roadmap

### Recently Fixed
- ✅ Migrated from `javax.persistence` to `jakarta.persistence`
- ✅ Fixed transaction scope — each write operation now opens/commits its own transaction instead of one transaction wrapping the entire menu loop
- ✅ Added `@ManyToOne`/`@OneToOne` mappings that were previously missing on `Trip` and `BusCompany`
- ✅ Replaced Haversine-only distance with real route distance via OSRM, with automatic fallback
- ✅ Added `isTripActive()` check to prevent deleting trips with existing ticket sales
- ✅ Fixed `BigDecimal` price validation to use `compareTo()` instead of `intValue()`

### In Progress
- [ ] Extract a `service/` layer (`TripService`, `TicketService`, `AuthService`, `DistanceService`) to remove business logic from UI classes
- [ ] Replace generic `RuntimeException`/`NullPointerException` throws with the custom exceptions in `exception/`
- [ ] Decompose large UI methods (`createTrip`, `buyBusTickets`) into smaller single-purpose methods

### Roadmap
- [ ] Add password hashing (BCrypt)
- [ ] Add input validation layer
- [ ] Add unit tests (JUnit 5 + Mockito)
- [ ] Dockerize the application
- [ ] Replace console UI with a REST API (Spring Boot)
- [ ] Add MongoDB-based audit logging
- [ ] Deploy to AWS

---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey covering JPA, Hibernate, Maven, external API integration, and professional software architecture patterns.
