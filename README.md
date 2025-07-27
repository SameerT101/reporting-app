# Reporting Application

Report application with Auth0 authentication, PDF report generation, and email functionality - all packaged in Docker
containers.

## Quick Start

### Prerequisites

- Docker (20.10 or later)
- Docker Compose (2.0 or later)

### One-Command Deployment

```bash
# Clone the repository
git clone <repository-url>
cd reportapp

# Start the entire application stack (first time)
./start-all.sh
```

The application will be available at `http://localhost:3000`

## Overview

The Docker setup includes everything you need:

- ** Reporting App** - Main application on port 3000
- ** PostgreSQL** - Database on port 5432
- ** RabbitMQ** - Message queue on port 5672 (UI: 15672)
- ** MailHog** - Email testing server on port 8025

## 🔧 Services & Ports

| Service     | Port  | URL                    | Credentials                    |
|-------------|-------|------------------------|--------------------------------|
| Main App    | 3000  | http://localhost:3000  | noname@acme.com / Passw0rd$123 |
| MailHog UI  | 8025  | http://localhost:8025  | None                           |
| RabbitMQ UI | 15672 | http://localhost:15672 | guest / guest                  |
| PostgreSQL  | 5432  | localhost:5432         | demo / demo                    |

## 🏃‍♂️ Usage

### 1. Web Interface

- Navigate to http://localhost:3000
- Login with test credentials: `noname@acme.com` / `Passw0rd$123`
- Generate and email PDF reports

### 2. API Interface

Use the provided curl examples on the home page or:

```bash
# Get access token
curl --location 'http://localhost:3000/api/auth/api-login-client' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'username=noname@acme.com' \
--data-urlencode 'password=Passw0rd$123'

# Download PDF report
curl -X GET http://localhost:3000/api/reports/pdf \
  -H "Authorization: Bearer TOKEN" \
  -o report.pdf

# Email PDF report
curl -X POST http://localhost:3000/api/reports/email \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"reportType":"product_of_all_categories","userEmail":"test@example.com"}'
```

## 🛠️ Management Commands

```bash
# Start all services (first time setup)
./start-all.sh

# Restart just the Spring Boot app (after code changes)
./start.sh

# Stop just the Spring Boot app
./stop.sh

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# View app logs only
docker-compose logs -f app

# View logs for specific service
docker-compose logs -f app

# Restart a specific service
docker-compose restart app

# Check service status
docker-compose ps

# Complete cleanup (removes data volumes)
docker-compose down -v
```

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────┐    ┌─────────────┐
│   Web Browser   │────│              │────│ PostgreSQL  │
│  localhost:3000 │    │     App      │    │   Database  │
└─────────────────┘    │              │    └─────────────┘
                       │              │    
┌─────────────────┐    │              │    ┌─────────────┐
│   API Client    │────│              │────│  RabbitMQ   │
│ (curl/Postman)  │    │              │    │   Queue     │
│ localhost:3000  │    │              │    │             │
└─────────────────┘    └──────────────┘    └─────────────┘
                              │             
                       ┌──────────────┐    
                       │   MailHog    │    
                       │ Email Server │    
                       └──────────────┘    
```

## Authentication

The application supports dual authentication:

### Session-Based (Web)

- OAuth2 login through Auth0
- Used for web interface
- Automatic session management

### JWT Bearer Token (API)

- Client credentials flow
- Used for API calls
- Token-based authentication

## Testing Email

1. Send an email report through the web interface
2. Open MailHog at http://localhost:8025
3. View the received email with PDF attachment

## Development

### Environment Variables

All configuration is handled through environment variables in `docker-compose.yml`:

- `SPRING_DATASOURCE_URL` - Database connection
- `SPRING_RABBITMQ_HOST` - Message queue host
- `SPRING_MAIL_HOST` - Email server host
- `AUTH0_*` - Auth0 configuration

### Building Locally

```bash
# Build without cache
docker-compose build --no-cache

# Build and start
docker-compose up --build
```

## Troubleshooting

### Application not starting?

```bash
# Check logs
docker-compose logs app

# Ensure all dependencies are healthy
docker-compose ps
```

### Database connection issues?

```bash
# Restart PostgreSQL
docker-compose restart postgres

# Check database status
docker-compose logs postgres
```

## Features

- **PDF Generation** - JasperReports-based PDF creation
- **Email Reports** - Asynchronous email sending with RabbitMQ
- **Auth0 Integration** - Modern OAuth2/OIDC authentication
- **Dual Auth Support** - Both session and token-based authentication
- **Docker Packaging** - Complete containerized deployment
- **Health Checks** - Built-in health monitoring
- **Auto-restart** - Services restart automatically on failure

## Tech Stack

- **Backend**: Spring Boot 3.5.3, Java 21
- **Database**: PostgreSQL 15
- **Queue**: RabbitMQ 3.12
- **Email**: MailHog (testing)
- **Auth**: Auth0 OAuth2
- **Reports**: JasperReports
- **Frontend**: Thymeleaf + Bootstrap 5
- **Containerization**: Docker + Docker Compose

