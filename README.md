# Smart Guide POC - Banking Product Recommendation API (Java Spring Boot)

AI-powered Islamic banking product recommendation system that processes natural language input and recommends suitable banking products.

## Overview

This is a Java Spring Boot conversion of the original Python FastAPI project, designed for enterprise deployment with improved scalability and maintainability.

## Features

- Natural language intent extraction using LLM (Azure OpenAI or Ollama)
- Rule-based product category mapping
- Intelligent product ranking algorithm
- Support for English and Arabic inputs
- Fast response times (<1.5 seconds)
- 100% Sharia-compliant product recommendations
- OpenAPI/Swagger documentation
- Flyway database migrations
- Comprehensive error handling

## Technology Stack

- **Java**: 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven
- **Database**: PostgreSQL 15+
- **ORM**: Spring Data JPA / Hibernate
- **Migration**: Flyway
- **Documentation**: SpringDoc OpenAPI 3
- **HTTP Client**: WebClient (Spring WebFlux)
- **JSON Processing**: Jackson

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 15+
- Azure OpenAI API key (or Ollama for local development)

## Quick Start

### 1. Clone the Repository

```bash
cd productRecommenderAI-springboot
```

### 2. Configure Database

Create PostgreSQL database:

```bash
createdb smart_guide_poc
```

### 3. Configure Environment

Copy `.env.example` to `.env` and configure:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smart_guide_poc
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# LLM Provider
LLM_PROVIDER=ollama  # or 'azure'

# Azure OpenAI (if using Azure)
AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com/
AZURE_OPENAI_API_KEY=your-api-key
AZURE_OPENAI_DEPLOYMENT=your-deployment-name

# Ollama (if using Ollama)
OLLAMA_HOST=http://localhost:11434
OLLAMA_MODEL=llama3.2
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/product-recommender-poc-1.0.0.jar
```

The application will start on `http://localhost:8080`

### 6. Database Migration

Flyway will automatically run migrations on startup. The database schema and seed data will be created automatically.

## Using Ollama (Local LLM)

### 1. Install Ollama

Visit [https://ollama.ai](https://ollama.ai) for installation instructions.

### 2. Pull Model

```bash
ollama pull llama3.2
```

### 3. Start Ollama Server

```bash
ollama serve
```

### 4. Configure Application

Set in `application.yml` or `.env`:

```yaml
app:
  llm:
    provider: ollama
    ollama:
      host: http://localhost:11434
      model: llama3.2
```

## API Usage

### Health Check

```bash
curl http://localhost:8080/health
```

### Get Product Recommendations

```bash
curl -X POST http://localhost:8080/api/v1/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "userInput": "I want to travel to Brazil",
    "language": "en"
  }'
```

### Example Response

```json
{
  "status": "success",
  "intent": {
    "detectedIntent": "TRAVEL",
    "confidence": 0.96,
    "entities": {
      "destination": "Brazil"
    }
  },
  "recommendations": [
    {
      "rank": 1,
      "productId": 1,
      "productCode": "CC_TRAVEL_01",
      "productName": "Voyager Travel Credit Card",
      "category": "CREDIT_CARD",
      "islamicStructure": "Murabaha",
      "relevanceScore": 0.92,
      "reason": "Perfect for travelers with Murabaha structure and travel benefits",
      "keyBenefits": [
        "5% cashback on international travel",
        "No foreign transaction fees",
        "Airport lounge access worldwide"
      ],
      "annualFee": 150.0,
      "minIncome": 50000.0
    }
  ],
  "processingTimeMs": 1240
}
```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/docs
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Project Structure

```
productRecommenderAI-springboot/
├── src/
│   ├── main/
│   │   ├── java/com/smartguide/poc/
│   │   │   ├── SmartGuidePocApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── LLMConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── HealthController.java
│   │   │   │   └── RecommendationController.java
│   │   │   ├── dto/
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── IntentData.java
│   │   │   │   ├── ProductRecommendation.java
│   │   │   │   ├── RecommendationRequest.java
│   │   │   │   ├── RecommendationResponse.java
│   │   │   │   └── UserContext.java
│   │   │   ├── entity/
│   │   │   │   ├── IntentCategoryMapping.java
│   │   │   │   └── Product.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/
│   │   │   │   ├── IntentCategoryMappingRepository.java
│   │   │   │   └── ProductRepository.java
│   │   │   └── service/
│   │   │       ├── LLMService.java
│   │   │       ├── ProductService.java
│   │   │       └── RulesEngine.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           ├── V1__Create_tables.sql
│   │           └── V2__Seed_data.sql
│   └── test/
│       └── java/com/smartguide/poc/
├── pom.xml
├── .env.example
├── .gitignore
└── README.md
```

## Configuration

### Application Properties

Key configuration in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smart_guide_poc
    username: postgres
    password: postgres

server:
  port: 8080

app:
  llm:
    provider: ollama  # or 'azure'
    ollama:
      host: http://localhost:11434
      model: llama3.2
```

## Building for Production

### Build JAR

```bash
mvn clean package -DskipTests
```

The JAR file will be created in `target/product-recommender-poc-1.0.0.jar`

### Run Production Build

```bash
java -jar target/product-recommender-poc-1.0.0.jar
```

### Docker Support (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/product-recommender-poc-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
docker build -t smart-guide-poc .
docker run -p 8080:8080 smart-guide-poc
```

## Testing

### Run Tests

```bash
mvn test
```

### Run with Coverage

```bash
mvn test jacoco:report
```

## Differences from Python Version

| Aspect | Python (FastAPI) | Java (Spring Boot) |
|--------|------------------|-------------------|
| Language | Python 3.10+ | Java 17 |
| Framework | FastAPI | Spring Boot 3.2 |
| ORM | SQLAlchemy | JPA/Hibernate |
| Async | Native async/await | Synchronous (can add WebFlux) |
| Type System | Dynamic + Pydantic | Static + Bean Validation |
| Migration | Manual SQL | Flyway |
| DI Container | FastAPI Depends | Spring IoC |
| Enterprise Support | Limited | Extensive |

## Performance Metrics

- Backend latency: <1.5 seconds (excluding LLM call)
- Supports 100+ concurrent requests
- Database queries optimized with indexes
- Intent extraction accuracy: Target 85%+

## Troubleshooting

### Database Connection Issues

Ensure PostgreSQL is running and credentials are correct in `application.yml` or `.env`.

### LLM Service Timeout

Increase timeout in `application.yml`:

```yaml
app:
  llm:
    ollama:
      timeout: 60000  # 60 seconds
```

### Port Already in Use

Change server port in `application.yml`:

```yaml
server:
  port: 8081
```

## Contributing

This is a POC project. For production use, consider:

- Adding comprehensive unit and integration tests
- Implementing authentication/authorization
- Adding request rate limiting
- Implementing caching (Redis)
- Adding monitoring (Prometheus/Grafana)
- Implementing circuit breakers
- Adding API versioning strategy

## License

This is a POC project for demonstration purposes.

## Support

For issues or questions, please refer to the project documentation or contact the development team.
