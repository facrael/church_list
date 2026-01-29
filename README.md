# Religious Marketplace API

Backend API for the Religious Marketplace mobile application - a platform for finding temples, booking religious services, and making online payments.

## Tech Stack

- **Framework**: FastAPI
- **Database**: PostgreSQL + PostGIS (geospatial)
- **Cache**: Redis
- **Auth**: JWT (access + refresh tokens)
- **ORM**: SQLAlchemy 2.0 (async)

## Quick Start

### 1. Prerequisites

- Python 3.11+
- Docker & Docker Compose (for database)

### 2. Setup

```bash
# Clone and enter directory
cd !!!Church

# Create virtual environment
python -m venv venv
source venv/bin/activate  # Linux/Mac
# or
venv\Scripts\activate  # Windows

# Install dependencies
pip install -r requirements.txt

# Copy environment file
cp .env.example .env
# Edit .env with your settings
```

### 3. Start Database

```bash
# Start PostgreSQL and Redis with Docker
docker-compose up -d
```

### 4. Run the API

```bash
# Run with auto-reload (development)
uvicorn app.main:app --reload

# Or run directly
python -m app.main
```

### 5. Seed Test Data

```bash
python -m scripts.seed_data
```

### 6. Access API

- **API**: http://localhost:8000
- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc

## Test Accounts

After seeding:
- Admin: `admin@example.com` / `admin123`
- User: `user@example.com` / `user123`
- Church Admin: `church_admin@example.com` / `church123`

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

### Churches
- `GET /api/v1/churches` - List churches
- `GET /api/v1/churches/nearby` - Find nearby churches
- `GET /api/v1/churches/{id}` - Get church details
- `POST /api/v1/churches/{id}/favorite` - Add to favorites

### Services
- `GET /api/v1/services` - List services
- `GET /api/v1/services/{id}` - Get service details

### Bookings
- `POST /api/v1/bookings` - Create booking
- `GET /api/v1/bookings` - List user's bookings
- `GET /api/v1/bookings/{id}` - Get booking details
- `DELETE /api/v1/bookings/{id}` - Cancel booking

### Users
- `GET /api/v1/users/me` - Get profile
- `PATCH /api/v1/users/me` - Update profile
- `GET /api/v1/users/me/favorites` - Get favorite churches

## Project Structure

```
!!!Church/
├── app/
│   ├── api/
│   │   ├── routes/
│   │   │   ├── auth.py
│   │   │   ├── bookings.py
│   │   │   ├── churches.py
│   │   │   ├── services.py
│   │   │   └── users.py
│   │   └── __init__.py
│   ├── core/
│   │   └── security.py
│   ├── models/
│   │   ├── booking.py
│   │   ├── church.py
│   │   ├── content.py
│   │   ├── event.py
│   │   ├── payment.py
│   │   ├── service.py
│   │   └── user.py
│   ├── schemas/
│   │   ├── booking.py
│   │   ├── church.py
│   │   ├── common.py
│   │   ├── service.py
│   │   └── user.py
│   ├── config.py
│   ├── database.py
│   └── main.py
├── scripts/
│   └── seed_data.py
├── .env.example
├── .gitignore
├── docker-compose.yml
├── requirements.txt
└── README.md
```

## Supported Religions

- **Orthodox Christianity** (православие)
- **Islam** (ислам)
- **Judaism** (иудаизм)

## License

Private - All rights reserved
