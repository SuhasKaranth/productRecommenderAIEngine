# Quick Start Guide

## Prerequisites Checklist

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] PostgreSQL 15+ running
- [ ] Ollama installed (or Azure OpenAI credentials)

## 5-Minute Setup

### Step 1: Create Database

```bash
# Create database
createdb smart_guide_poc

# Verify connection
psql -d smart_guide_poc -c "SELECT version();"
```

### Step 2: Configure Environment

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your database password
# If using Ollama (default), no other changes needed
```

### Step 3: Start Ollama (if using local LLM)

```bash
# In a separate terminal
ollama pull llama3.2
ollama serve
```

### Step 4: Build and Run

```bash
# Build the project
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

### Step 5: Test the API

```bash
# Health check
curl http://localhost:8080/health

# Test recommendation
curl -X POST http://localhost:8080/api/v1/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "userInput": "I want to travel to Brazil",
    "language": "en"
  }'
```

### Step 6: View API Documentation

Open in browser: http://localhost:8080/docs

## Common Issues

### "Connection refused" to PostgreSQL

```bash
# Check if PostgreSQL is running
pg_isready

# Start PostgreSQL (macOS)
brew services start postgresql@15
```

### "Port 8080 already in use"

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8081
```

### Ollama connection error

```bash
# Check Ollama is running
curl http://localhost:11434/api/tags

# Start Ollama
ollama serve
```

## Next Steps

1. **Explore the API**: Visit http://localhost:8080/docs
2. **Review the code**: Check `src/main/java/com/smartguide/poc/`
3. **Customize products**: Edit `src/main/resources/db/migration/V2__Seed_data.sql`
4. **Add tests**: Create tests in `src/test/java/`

## IntelliJ IDEA Setup

1. Open IntelliJ IDEA
2. File → Open → Select `productRecommenderAI-springboot` folder
3. Wait for Maven to import dependencies
4. Right-click on `SmartGuidePocApplication.java` → Run
5. Application will start on port 8080

## VS Code Setup

1. Install "Extension Pack for Java"
2. Open folder in VS Code
3. Run Maven commands from terminal
4. Use "Run" button in `SmartGuidePocApplication.java`

## Production Deployment

```bash
# Build production JAR
mvn clean package -DskipTests

# Run production build
java -jar target/product-recommender-poc-1.0.0.jar
```

## Environment Variables

Key variables you can set:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smart_guide_poc
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# LLM Provider
LLM_PROVIDER=ollama

# Server
SERVER_PORT=8080
```

Happy coding! 🚀
