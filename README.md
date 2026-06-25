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

BusApp is a backend-focused Java project built to practice JPA/Hibernate, the DAO pattern, entity relationships, transaction management, service layer design, and external API integration. It simulates a real-world bus ticketing platform where two types of users interact:

- **Travelers** — can browse available trips, buy tickets, and cancel with credit refund
- **Bus Companies** — can create and manage trips between Brazilian cities, with real-route pricing

---

## Features

### Traveler
- Sign up / login with email and password
- Email availability check on sign up (prevents duplicate accounts)
- Buy tickets for available trips (can buy for another person)
- View purchased tickets, with optional filter for canceled ones
- Cancel tickets up to 1 hour before departure (with automatic credit refund)
- Update profile (name, CPF, birth date, telephone, password) or delete account

### Bus Company
- Sign up / login with email and password
- Email availability check on sign up (prevents duplicate accounts)
- Create trips with auto-suggested price based on **real driving distance** (via OSRM)
- View and manage existing trips (edit origin, destination, departure time, price)
- Delete trips — blocked automatically if the trip already has tickets sold
- Update company profile (legal name, trading name, CNPJ, telephone, password)

### System
- Real route distance via the **OSRM API**, with automatic fallback to **Haversine**
- Automatic trip category classification: `INTERCITY` or `INTERSTATE`
- Automatic code generation for trips and tickets
- Per-operation transaction handling with rollback on failure
- Custom domain exceptions for all business rule violations
- Credentials loaded from **environment variables** (no hardcoded secrets)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21+ |
| ORM | Hibernate / Jakarta Persistence (JPA) |
| Database | MySQL |
| HTTP Client | Java 11+ native `HttpClient` |
| JSON Mapping | Jackson (`jackson-databind` 2.18.3) |
| External API | OSRM (routing/distance) |
| Build | Maven |
| Utilities | Lombok, FlatLaf (Swing UI prototype) |

---

## Project Structure

```
src/main/java/br/com/javamastery/
│
├── bytebank/                # UI layer — input/output only, no business logic
│   ├── MainScreen.java          # Traveler flow
│   ├── BusCompanyMainScreen.java   # Bus company flow
│   └── AddressMainScreen.java   # Address/city search
│
├── client/                  # External API integration
│   ├── OsrmClient.java          # OSRM HTTP client
│   └── dto/
│       └── OsrmResponse.java    # OSRM JSON response mapping
│
├── dao/                     # Data Access Layer — queries only
│   ├── AddressDAO.java
│   ├── BusCompanyDAO.java
│   ├── BusTicketDAO.java
│   ├── EmailDAO.java
│   ├── TravelerDAO.java
│   └── TripDAO.java
│
├── exception/               # Custom domain exceptions
│   ├── CancellationDeadlineExceededException.java
│   ├── CityNotFoundException.java
│   ├── EmailAlreadyExistsException.java
│   ├── InvalidCredentialsException.java
│   ├── InvalidPriceException.java
│   ├── TicketNotFoundException.java
│   ├── TripAlreadySoldException.java
│   └── TripNotFoundException.java
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
├── service/                 # Business logic layer
│   ├── AuthService.java         # login(), emailExists(), checkEmailAvailable()
│   ├── TicketService.java       # buyTicket(), cancelTicket()
│   └── TripService.java         # suggestPrice(), createTrip(), searchTrips(), searchSingleTrip()
│
└── util/                    # Utilities
    ├── JPAUtils.java            # EntityManagerFactory singleton
    └── ValidationUtils.java     # Random code generation
```

---

## Database Schema

```
states          cities          traveler        email
─────────       ──────────      ────────────    ──────────
state_code PK   IBGE_code PK    id PK           id PK
uf              city            name            email
name            latitude        birth_date      password
region          longitude       cpf             email_type
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

Create a MySQL database and import your schema.

### 3. Configure environment variables

See [Environment Variables](#environment-variables) below.

### 4. Build and run

```bash
mvn clean compile

# Traveler flow
mvn exec:java -Dexec.mainClass="br.com.javamastery.bytebank.MainScreen"

# Bus company flow
mvn exec:java -Dexec.mainClass="br.com.javamastery.bytebank.BusCompanyMainScreen"
```

---

## Environment Variables

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

### Recently Completed
- ✅ Migrated from `javax.persistence` to `jakarta.persistence`
- ✅ Fixed transaction scope — each write operation owns its own transaction with rollback on failure
- ✅ Added missing JPA mappings (`@ManyToOne`, `@OneToOne`)
- ✅ Real route distance via OSRM with Haversine fallback
- ✅ `isTripActive()` guard prevents deleting trips with ticket sales
- ✅ Fixed `BigDecimal` comparison (`compareTo` instead of `intValue`)
- ✅ Fixed cancel/exit detection (`equalsIgnoreCase("C")`)
- ✅ 8 typed domain exceptions in `exception/`
- ✅ `AuthService` — login, emailExists, checkEmailAvailable
- ✅ `TripService` — suggestPrice, createTrip, searchTrips, searchSingleTrip
- ✅ `TicketService` — buyTicket, cancelTicket (single DAO query on cancel)
- ✅ `MainScreen.buyBusTickets()` decomposed into: `collectOriginCity`, `collectDestinationCity`, `collectTrip`, `collectDepartureDate`, `collectTraveler`
- ✅ `BusCompanyMainScreen.createTrip()` decomposed into: `collectOriginCity`, `collectDestinationCity`, `askPriceOrAcceptSuggestion`, `askDepartureTime`

### In Progress
- [ ] `TravelerService` — signUp(), updateProfile(), deleteProfile()
- [ ] `BusCompanyService` — signUp(), updateProfile()
- [ ] Move remaining `em.getTransaction()` calls out of UI methods
---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey covering JPA, Hibernate, Maven, service layer design, external API integration, and professional software architecture patterns.
