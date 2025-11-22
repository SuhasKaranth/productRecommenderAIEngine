# Product Scraper Admin UI

React-based admin interface for managing web scraping and product approval workflow.

## Features

- **Dashboard**: Overview of staging products with statistics
- **Start Scrape**: Trigger new scraping jobs for configured websites
- **Review Products**: Approve, edit, or reject scraped products

## Development

### Prerequisites

- Node.js 16+ and npm
- Backend services running (main app on port 8080, scraper on port 8081)

### Installation

```bash
cd frontend
npm install
```

### Running Locally

```bash
npm start
```

The app will open at http://localhost:3000 and proxy API requests to the backend.

### Building for Production

```bash
npm run build
```

The optimized production build will be in the `build/` directory.

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── pages/
│   │   ├── Dashboard.jsx       # Dashboard with statistics
│   │   ├── ScrapeForm.jsx      # Form to trigger scraping
│   │   └── StagingReview.jsx   # Product review & approval
│   ├── services/
│   │   └── api.js              # API integration with axios
│   ├── App.jsx                 # Main app component
│   └── index.js                # Entry point
└── package.json
```

## API Endpoints Used

### Staging Products API (Main App - Port 8080)
- `GET /api/admin/staging` - List all staging products
- `GET /api/admin/staging/{id}` - Get product details
- `PUT /api/admin/staging/{id}` - Update product
- `POST /api/admin/staging/{id}/approve` - Approve product
- `POST /api/admin/staging/bulk-approve` - Bulk approve
- `POST /api/admin/staging/{id}/reject` - Reject product
- `DELETE /api/admin/staging/{id}` - Delete product
- `GET /api/admin/staging/stats` - Get statistics

### Scraper API (Scraper Service - Port 8081)
- `POST /api/scraper/trigger/{websiteId}` - Start scraping
- `GET /api/scraper/status/{jobId}` - Check job status
- `GET /api/scraper/sources` - List configured websites
- `GET /api/scraper/history/{websiteId}` - View scrape history

## Workflow

1. **Start Scraping**: Select a website and trigger scraping
2. **Wait for Completion**: Products are scraped and saved to staging
3. **Review Products**: View AI-categorized products with quality scores
4. **Edit if Needed**: Modify product details before approval
5. **Approve/Reject**: Individual or bulk actions to move products to production

## Technologies

- React 18
- Material-UI (MUI) 5
- Axios for API calls
- React Router (if needed for future routing)

## Integration with Spring Boot

When building with Maven, this frontend is automatically built and served from the main Spring Boot application at `/ui`.
