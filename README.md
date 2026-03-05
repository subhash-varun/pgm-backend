# PG Management System API
A comprehensive REST API for managing Paying Guest (PG) accommodations with role-based access control, built with Spring Boot 3.5.6.

## 📋 Overview

This PG Management System provides a complete solution for managing:
- **Admin & Staff Management**: User accounts with different roles and permissions
- **Room Management**: Track room availability, types, and facilities
- **Tenant Management**: Handle tenant check-ins, check-outs, and personal details
- **Payment Tracking**: Record rent payments and generate receipts
- **Inventory Management**: Track room furnishings and maintenance status
- **Role-Based Security**: JWT authentication with granular permission control

## ✨ Features

### 🔐 Security & Authentication
- JWT-based authentication
- Role-based access control (RBAC)
- Method-level permission checking
- Secure password encryption

### 👥 User Management
- Admin users with full system access
- Staff users with operational permissions
- Dynamic permission assignment
- User role management

### 🏠 Accommodation Management
- Multiple room types (Single, Double, Shared)
- Room status tracking (Available, Occupied, Maintenance)
- Facility management
- Rent amount configuration

### 👨‍👩‍👧‍👦 Tenant Operations
- Tenant registration and profile management
- Check-in/check-out tracking
- ID proof verification
- Deposit management

### 💰 Financial Management
- Rent payment recording
- Payment method tracking
- Receipt generation
- Payment status monitoring

### 📦 Inventory Tracking
- Room furnishings inventory
- Condition status monitoring
- Maintenance scheduling
- Item quantity management

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **ORM**: Hibernate/JPA
- **Security**: Spring Security + JWT
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Validation**: Bean Validation
- **AOP**: AspectJ for permission checking

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 17** or higher
- **MySQL 8.0+** database server
- **Maven 3.6+** (or use included Maven wrapper)
- **Git** for version control

## 🚀 Installation & Setup

### Option 1: Quick Setup (Recommended)
See `QUICKSTART.md` for the fastest way to get started.

### Option 2: Manual Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/subhash-varun/pgm-backend.git
cd pgm-backend
```

#### 2. Database Setup

**Manual MySQL Setup:**
1. Install MySQL Server on your system
2. Create a database named `pgm_db`
3. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pgm_db?createDatabaseIfNotExist=true
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

**Using Docker:**
```bash
# Run MySQL in Docker
docker run --name pgm-mysql -e MYSQL_ROOT_PASSWORD=password123 -e MYSQL_DATABASE=pgm_db -p 3306:3306 -d mysql:8.0
```

#### 3. Build and Run

```bash
# Build the application
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```

### Option 3: Docker Compose (Easiest)

```bash
# Clone the repository
git clone https://github.com/subhash-varun/pgm-backend.git
cd pgm-backend

# Start with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f pgm-app
```

### Quick Start Scripts

**Windows:**
```cmd
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

These scripts will check prerequisites and start the application automatically.

### 5. Verify Installation

Once the application starts successfully, you should see logs like:
```
✅ Created 24 permissions
✅ Created 4 roles with assigned permissions
✅ Created default admin user: admin@pgm.com / admin123
✅ Created 3 staff members
✅ Created 6 rooms
✅ Created 3 tenants and updated room statuses
✅ Created 9 inventory items
✅ Created 4 payment records
Data seeding completed successfully!
```

## 📚 API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Specification
Download the OpenAPI spec at:
```
http://localhost:8080/v3/api-docs
```

## 👤 Default Users & Credentials

The application comes pre-seeded with the following users:

### Super Admin
- **Email**: `admin@pgm.com`
- **Password**: `admin123`
- **Role**: Super Admin (Full access to all features)

### Staff Users
1. **Rajesh Kumar** (Manager)
   - Email: `rajesh@pgm.com`
   - Password: `rajesh123`

2. **Priya Sharma** (Staff)
   - Email: `priya@pgm.com`
   - Password: `priya123`

3. **Amit Singh** (Staff)
   - Email: `amit@pgm.com`
   - Password: `amit123`

## 🔗 API Endpoints Overview

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration (admin only)

### Admin Management
- `GET /api/admins` - List all admins
- `POST /api/admins` - Create new admin
- `GET /api/admins/{id}` - Get admin details
- `PUT /api/admins/{id}` - Update admin
- `DELETE /api/admins/{id}` - Delete admin

### Staff Management
- `GET /api/staff` - List all staff
- `POST /api/staff` - Create new staff member
- `GET /api/staff/{id}` - Get staff details
- `PUT /api/staff/{id}` - Update staff
- `DELETE /api/staff/{id}` - Delete staff

### Room Management
- `GET /api/rooms` - List all rooms
- `POST /api/rooms` - Create new room
- `GET /api/rooms/{id}` - Get room details
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room

### Tenant Management
- `GET /api/tenants` - List all tenants
- `POST /api/tenants` - Register new tenant
- `GET /api/tenants/{id}` - Get tenant details
- `PUT /api/tenants/{id}` - Update tenant
- `DELETE /api/tenants/{id}` - Remove tenant

### Payment Management
- `GET /api/payments` - List all payments
- `POST /api/payments` - Record new payment
- `GET /api/payments/{id}` - Get payment details
- `PUT /api/payments/{id}` - Update payment
- `DELETE /api/payments/{id}` - Delete payment

### Inventory Management
- `GET /api/inventory` - List all inventory items
- `POST /api/inventory` - Add new inventory item
- `GET /api/inventory/{id}` - Get inventory item details
- `PUT /api/inventory/{id}` - Update inventory item
- `DELETE /api/inventory/{id}` - Remove inventory item

### Role & Permission Management
- `GET /api/roles` - List all roles
- `POST /api/roles` - Create new role
- `GET /api/permissions` - List all permissions
- `GET /api/users/roles` - Get user roles

## 🧪 Testing the API

### 1. Authentication
```bash
# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@pgm.com",
    "password": "admin123"
  }'
```

### 2. Using JWT Token
```bash
# Include the token in subsequent requests
curl -X GET http://localhost:8080/api/rooms \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Sample API Calls

#### Get All Rooms
```bash
curl -X GET "http://localhost:8080/api/rooms" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Create a New Room
```bash
curl -X POST "http://localhost:8080/api/rooms" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomNumber": "401",
    "roomType": "SINGLE",
    "rentAmount": 6000.00,
    "status": "AVAILABLE",
    "facilities": "WiFi, AC, Attached Bathroom, Study Table"
  }'
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/pgm_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=password123

# JWT Configuration
jwt.secret=mySecretKey1234567890123456789012345678901234567890
jwt.expiration=86400000  # 24 hours in milliseconds

# Logging
logging.level.com.varun.pgm=DEBUG
```

### Environment Variables

The application now supports `.env` files. Copy the example file and customize it:

```bash
cp .env.example .env
```

The `.env` file contains:
- Database configuration
- JWT settings
- Server port
- Logging level

**Note**: The application will automatically load `.env` if present, otherwise use defaults.

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure MySQL is running
   - Verify database credentials
   - Check if database exists

2. **Port Already in Use**
   - Change the port in `application.properties`:
   ```properties
   server.port=8081
   ```

3. **JWT Token Issues**
   - Verify the JWT secret key is set correctly
   - Check token expiration time

4. **Permission Denied Errors**
   - Ensure user has required permissions
   - Check role assignments in the database

### Logs
Check application logs for detailed error information:
```bash
# View application logs
tail -f logs/application.log
```

## 📁 Project Files

- `README.md` - Comprehensive setup and usage guide
- `QUICKSTART.md` - Quick start guide for beginners
- `Dockerfile` - Docker image configuration
- `docker-compose.yml` - Docker Compose setup with MySQL
- `.env.example` - Environment variables template
- `HELP.md` - Default Spring Boot help documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the API documentation at `/swagger-ui/index.html`
- Review the application logs for error details

---

**Happy coding! 🎉**</content>
<parameter name="filePath">d:\SaaS Projects\starter-main\pgm\README.md
