# Digital Wallet Application Backend

## Overview
A robust digital wallet backend service built with Spring Boot and Apache Kafka, providing secure transaction processing and real-time payment capabilities.

## Tech Stack
- Java 17
- Spring Boot 3.2.x
- Apache Kafka
- PostgreSQL
- Maven
- JUnit 5
- Spring Security
- Spring Data JPA

## Features
- Real-time transaction processing
- Account management
- Balance tracking
- Transaction history
- Payment processing
- User authentication and authorization
- Event-driven architecture using Kafka

## Prerequisites
- JDK 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- Apache Kafka 3.x
- Docker (optional, for containerization)

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── wallet/
│   │           ├── WalletApplication.java
│   │           ├── config/
│   │           │   ├── KafkaConfig.java
│   │           │   └── SecurityConfig.java
│   │           ├── controller/
│   │           │   ├── WalletController.java
│   │           │   └── TransactionController.java
│   │           ├── model/
│   │           │   ├── Wallet.java
│   │           │   └── Transaction.java
│   │           ├── repository/
│   │           │   ├── WalletRepository.java
│   │           │   └── TransactionRepository.java
│   │           └── service/
│   │               ├── WalletService.java
│   │               └── TransactionService.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── com/
            └── wallet/
                └── service/
                    ├── WalletServiceTest.java
                    └── TransactionServiceTest.java
```

## Configuration
### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wallet_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Kafka Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: wallet-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/wallet-app.git
cd wallet-app
```

### 2. Set Up Database
```bash
# Create PostgreSQL database
createdb wallet_db
```

### 3. Configure Environment Variables
Create a `.env` file in the project root:
```
DB_USERNAME=your_username
DB_PASSWORD=your_password
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 4. Start Kafka
```bash
# Using Docker
docker-compose up -d kafka zookeeper

# Or start Kafka manually
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

### 5. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### Wallet Operations
- POST `/api/v1/wallets` - Create new wallet
- GET `/api/v1/wallets/{id}` - Get wallet details
- PUT `/api/v1/wallets/{id}/balance` - Update wallet balance
- GET `/api/v1/wallets/{id}/transactions` - Get wallet transactions

### Transaction Operations
- POST `/api/v1/transactions` - Create new transaction
- GET `/api/v1/transactions/{id}` - Get transaction details
- GET `/api/v1/transactions/status/{status}` - Get transactions by status

## Testing
```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## Monitoring and Metrics
The application includes Spring Boot Actuator endpoints for monitoring:
- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus endpoint: `/actuator/prometheus`

## Security
- JWT-based authentication
- Role-based access control
- API request rate limiting
- Input validation
- SQL injection prevention
- XSS protection

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.
