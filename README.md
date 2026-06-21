# 🚌 BusApp — Bus Ticket Management System

A console-based Java application for managing bus trips and tickets, featuring authentication, trip booking, cancellation with credit balance, and real-world distance calculation via the OSRM routing API.

\---

## 📋 Table of Contents

* [About the Project](#about-the-project)
* [Features](#features)
* [Tech Stack](#tech-stack)
* [Project Structure](#project-structure)
* [Database Schema](#database-schema)
* [Getting Started](#getting-started)
* [Environment Variables](#environment-variables)
* [Known Issues \& Roadmap](#known-issues--roadmap)

\---

## About the Project

BusApp is a backend-focused Java project built to practice JPA/Hibernate, the DAO pattern, entity relationships, transaction management, and external API integration. It simulates a real-world bus ticketing platform where two types of users interact:

* **Travelers** — can browse available trips, buy tickets, and cancel with credit refund
* **Bus Companies** — can create and manage trips between Brazilian cities, with real-route pricing

\---

## Features

### Traveler

* Sign up / login with email and password
* Buy tickets for available trips (can buy for another person)
* View purchased tickets, with optional filter for canceled ones
* Cancel tickets up to 1 hour before departure (with automatic credit refund)
* Update profile (name, CPF, birth date, telephone, password) or delete account

### Bus Company

* Sign up / login with email and password
* Create trips between cities with auto-suggested price based on **real driving distance** (via OSRM)
* View and manage existing trips (edit origin, destination, departure time, price)
* Delete trips — blocked automatically if the trip already has tickets sold
* Update company profile (legal name, trading name, CNPJ, telephone, password)

### System

* Real route distance calculation via the **OSRM (Open Source Routing Machine)** API, with automatic fallback to the **Haversine formula** if the API is unavailable
* Automatic trip category classification: `INTERCITY` or `INTERSTATE`
* Automatic code generation for trips and tickets
* Per-operation transaction handling (no long-lived transactions wrapping menu loops)
* Brazilian city and state data with UF/name lookup
* Credentials loaded from **environment variables** (no hardcoded secrets)

\---

## Tech Stack

|Layer|Technology|
|-|-|
|Language|Java 21+|
|ORM|Hibernate / Jakarta Persistence (JPA)|
|Database|MySQL|
|HTTP Client|Java 11+ native `HttpClient`|
|JSON Mapping|Jackson (`jackson-databind`)|
|External API|OSRM (routing/distance)|
|Build|Maven|
|Utilities|Lombok, FlatLaf (Swing UI prototype)|

\---

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

> \*\*Note:\*\* A `service/` layer (`TripService`, `TicketService`, `AuthService`, `DistanceService`) is currently being introduced to separate business logic from the UI layer. See \[Roadmap](#known-issues--roadmap).

\---

## Database Schema

```
states          cities          traveler        email
─────────       ──────────      ────────────    ──────────
state\_code PK   IBGE\_code PK    id PK           id PK
uf              city            name            email
name            latitude        birth\_date      password
region          longitude       cpf              email\_type
                ddd             telephone
                state\_code FK   credits\_balance
                                email\_id FK
                                created\_at
                                edited\_at

bus\_company     trips            bus\_ticket
───────────     ──────────       ──────────────
id PK           id PK            id PK
legal\_name      code             code
trading\_name    origin\_city FK   traveler\_id FK
cnpj            destination\_city FK  trip\_id FK
email\_id FK     price            price
telephone       company\_id FK    departure\_date
created\_at      departure\_time   sale\_date
edited\_at       distance\_km      canceled
                category         cancel\_date
                active           edited\_at
                created\_at       buyer\_id
                edited\_at
```

\---

## Getting Started

### Prerequisites

* Java 21+
* Maven 3.8+
* MySQL 8+

### 1\. Clone the repository

```bash
git clone https://github.com/Emanoel-H/Java-Mastery.git
cd Java-Mastery
```

### 2\. Set up the database

Create a MySQL database and import your schema. Make sure the tables match the entities in `models/`.

### 3\. Configure environment variables

See [Environment Variables](#environment-variables) section below.

### 4\. Build and run

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="br.com.javamastery.bytebank.MainScreen"
```

> Run `BusCompanyMainScreen` instead if you want to access the bus company flow.

\---

## Environment Variables

The application reads database credentials from environment variables to avoid hardcoding secrets.

|Variable|Description|Example|
|-|-|-|
|`DB\_URL`|JDBC connection URL|`jdbc:mysql://localhost:3306/busapp`|
|`DB\_USER`|Database username|`root`|
|`DB\_PASS`|Database password|`yourpassword`|

**On Windows (PowerShell):**

```powershell
$env:DB\_URL="jdbc:mysql://localhost:3306/busapp"
$env:DB\_USER="root"
$env:DB\_PASS="yourpassword"
```

**On Linux/macOS:**

```bash
export DB\_URL="jdbc:mysql://localhost:3306/busapp"
export DB\_USER="root"
export DB\_PASS="yourpassword"
```

\---

## Known Issues \& Roadmap

### Recently Fixed

* ✅ Migrated from `javax.persistence` to `jakarta.persistence`
* ✅ Fixed transaction scope — each write operation now opens/commits its own transaction instead of one transaction wrapping the entire menu loop
* ✅ Added `@ManyToOne`/`@OneToOne` mappings that were previously missing on `Trip` and `BusCompany`
* ✅ Replaced Haversine-only distance with real route distance via OSRM, with automatic fallback
* ✅ Added `isTripActive()` check to prevent deleting trips with existing ticket sales
* ✅ Fixed `BigDecimal` price validation to use `compareTo()` instead of `intValue()`

\---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey covering JPA, Hibernate, Maven, external API integration, and professional software architecture patterns.

