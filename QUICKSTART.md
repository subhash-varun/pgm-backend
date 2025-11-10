# PG Management System - Quick Start Guide

## 🚀 Quick Setup (Windows)

### Prerequisites
- Java 17+ installed
- MySQL 8.0+ installed and running

### 1. Database Setup
```cmd
mysql -u root -p
CREATE DATABASE pgm_db;
exit;
```

### 2. Clone & Run
```cmd
git clone https://github.com/subhash-varun/pgm-backend.git
cd pgm-backend

# Optional: Configure environment (already has .env file)
# Edit .env with your database credentials if needed

# Quick start (Windows)
run.bat

# Or manual start
mvnw spring-boot:run
```

### 3. Access Application
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Login**: admin@pgm.com / admin123

## 🐳 Docker Setup (Alternative)

### Using Docker Compose
```bash
# Start the entire stack
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Docker Setup
```bash
# Build the application image
docker build -t pgm-app .

# Run MySQL
docker run --name pgm-mysql -e MYSQL_ROOT_PASSWORD=password123 -e MYSQL_DATABASE=pgm_db -p 3306:3306 -d mysql:8.0

# Run the application
docker run --name pgm-app -p 8080:8080 --link pgm-mysql:mysql -d pgm-app
```

## 📊 Sample Data

The application starts with pre-seeded data:

### Users
- **Super Admin**: admin@pgm.com / admin123
- **Manager**: rajesh@pgm.com / rajesh123
- **Staff**: priya@pgm.com / priya123

### Sample Rooms
- Room 101: Single, ₹5,000/month
- Room 102: Single, ₹5,500/month
- Room 201: Double, ₹8,000/month
- Room 202: Double, ₹8,500/month
- Room 301: Shared, ₹3,000/month
- Room 302: Shared, ₹3,200/month

### Sample Tenants
- Arun Kumar (Room 101)
- Meera Patel (Room 102)
- Vikram Singh (Room 201)

## 🔗 API Quick Reference

### Authentication
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@pgm.com","password":"admin123"}'
```

### Common Endpoints
- `GET /api/rooms` - List rooms
- `GET /api/tenants` - List tenants
- `GET /api/payments` - List payments
- `GET /api/staff` - List staff

## 🆘 Need Help?

1. Check application logs in the console
2. Visit Swagger UI for API documentation
3. Ensure MySQL is running on port 3306
4. Verify database credentials in application.properties</content>
<parameter name="filePath">d:\SaaS Projects\starter-main\pgm\QUICKSTART.md
