# Product Scraper Service

Web scraping service for automatically extracting Islamic banking product data from websites and storing them in the database.

## Features

- **Playwright-based scraping**: Modern, reliable web scraping with JavaScript support
- **Configuration-driven**: Define scraper behavior using simple YAML files
- **AI-powered enrichment**: Uses LLM to parse and validate scraped data
- **REST API**: Trigger and monitor scraping jobs via API
- **Deduplication**: Automatically handles duplicate products using product codes
- **Quality scoring**: Calculates data quality scores for scraped products
- **Job tracking**: Complete audit trail of all scraping activities

## Architecture

```
┌─────────────────┐
│  Scraper API    │
│  (Port 8081)    │
└────────┬────────┘
         │
         ├──> ScraperConfigLoader (YAML configs)
         │
         ├──> PlaywrightScraperEngine (Web scraping)
         │
         ├──> LLMDataEnricher (AI enrichment)
         │
         └──> ScraperDatabaseService (Persistence)
                     │
                     ├──> products table (shared with main app)
                     ├──> scrape_sources table
                     └──> scrape_logs table
```

## Configuration

### Application Configuration

Create a `.env` file in the project root (main app's .env is shared):

```bash
# Database (shared with main app)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smart_guide_poc
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Scraper Service Port
SCRAPER_SERVER_PORT=8081

# Main Service URL (for LLM API calls)
MAIN_SERVICE_URL=http://localhost:8080
```

### Website Scraper Configuration

Create YAML files in `src/main/resources/scraper-configs/` for each website:

```yaml
websiteId: "bank_abc"
websiteName: "ABC Islamic Bank"
baseUrl: "https://www.bank-abc.com"

navigation:
  startUrl: "https://www.bank-abc.com/products"
  waitAfterLoad: 2000

selectors:
  productList: ".product-card"
  productLink: "a.product-url"
  productName: "h1.title"
  category: ".category"
  description: ".description"
  # ... more selectors

mapping:
  defaultCategory: "FINANCING"
  shariaCertified: true

options:
  aiEnrichment: true
  headless: true
  timeout: 30000
  delayBetweenRequests: 1000
```

## Installation & Setup

### 1. Install Playwright Browsers

First time setup requires installing Playwright browsers:

```bash
cd product-scraper-service
mvn clean install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### 2. Build the Service

```bash
mvn clean package
```

### 3. Run the Service

```bash
mvn spring-boot:run
```

The service will start on port 8081 (or your configured port).

## API Endpoints

### Trigger Scraping

```bash
POST /api/scraper/trigger/{websiteId}
```

Example:
```bash
curl -X POST http://localhost:8081/api/scraper/trigger/example_bank
```

Response:
```json
{
  "jobId": "abc-123-def",
  "websiteId": "example_bank",
  "status": "STARTED",
  "message": "Scraping job started successfully"
}
```

### Check Job Status

```bash
GET /api/scraper/status/{jobId}
```

Example:
```bash
curl http://localhost:8081/api/scraper/status/abc-123-def
```

### List Configured Websites

```bash
GET /api/scraper/sources
```

### Get Scraping History

```bash
GET /api/scraper/history/{websiteId}
```

### Reload Configurations

```bash
POST /api/scraper/configs/reload
```

## How It Works

### 1. Scraping Flow

1. **Load Configuration**: YAML config loaded for the target website
2. **Navigate**: Playwright navigates to the product listing page
3. **Extract URLs**: Product URLs are extracted using configured selectors
4. **Scrape Products**: Each product page is visited and data extracted
5. **AI Enrichment** (optional): LLM parses and enriches the raw HTML
6. **Quality Scoring**: Data completeness score is calculated
7. **Persistence**: Products are saved/updated in the database
8. **Logging**: Job results are logged for audit

### 2. Data Quality Score

Products are scored 0.0-1.0 based on field completeness:
- All 14 fields filled = 1.0 (perfect)
- 7 fields filled = 0.5
- Only name and URL = 0.14

### 3. Deduplication Strategy

- Products are identified by `product_code`
- If product exists: Updates description, rates, and scrape metadata
- If new: Creates new product entry
- Source tracking: Each product tracks which website it came from

## Adding a New Website

1. **Inspect the website structure** using browser DevTools
2. **Create YAML config** in `scraper-configs/`:
   ```yaml
   websiteId: "new_bank_id"
   websiteName: "New Bank Name"
   # ... configure selectors
   ```

3. **Add to database**:
   ```sql
   INSERT INTO scrape_sources (website_id, website_name, base_url, config_path, active)
   VALUES ('new_bank_id', 'New Bank Name', 'https://newbank.com',
           'classpath:scraper-configs/new_bank_id.yml', true);
   ```

4. **Test the scraper**:
   ```bash
   curl -X POST http://localhost:8081/api/scraper/trigger/new_bank_id
   ```

## Troubleshooting

### Playwright Installation Issues

If browser installation fails:
```bash
# Install manually
npx playwright install chromium

# Or set environment variable
export PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=false
```

### Selector Not Found

- Inspect the actual HTML structure
- Websites change - selectors may need updates
- Try more generic selectors: `h1` instead of `.specific-class`
- Enable screenshot option to debug

### AI Enrichment Fails

- Check main service is running (port 8080)
- Verify MAIN_SERVICE_URL in configuration
- Can disable AI enrichment: `aiEnrichment: false`

## API Documentation

Full API documentation available at:
- **Swagger UI**: http://localhost:8081/docs
- **OpenAPI JSON**: http://localhost:8081/api-docs

## Development

### Project Structure

```
product-scraper-service/
├── src/main/java/com/smartguide/scraper/
│   ├── controller/         # REST API endpoints
│   ├── service/           # Business logic
│   │   ├── PlaywrightScraperEngine.java
│   │   ├── LLMDataEnricher.java
│   │   ├── ScraperConfigLoader.java
│   │   └── ScraperOrchestrationService.java
│   ├── model/             # Configuration models
│   ├── dto/               # API request/response DTOs
│   └── config/            # Spring configuration
├── src/main/resources/
│   ├── scraper-configs/   # YAML configurations per website
│   └── application.yml    # Application properties
└── pom.xml
```

### Technology Stack

- **Spring Boot 3.2**: Application framework
- **Playwright Java 1.40**: Web scraping
- **PostgreSQL**: Database (shared with main app)
- **Jackson YAML**: Configuration parsing
- **WebFlux**: HTTP client for LLM API calls
- **Lombok**: Code generation

## License

Part of the Smart Guide POC - Product Recommender System
