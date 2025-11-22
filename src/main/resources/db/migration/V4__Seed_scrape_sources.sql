-- Seed initial scrape sources for configured websites
-- These correspond to the YAML configuration files in scraper-configs/

INSERT INTO scrape_sources (website_id, website_name, base_url, config_path, active, created_at, updated_at)
VALUES
    ('example_bank', 'Example Islamic Bank', 'https://www.examplebank.com',
     'classpath:scraper-configs/example-bank.yml', true, NOW(), NOW()),

    ('maybank_islamic', 'Maybank Islamic', 'https://www.maybank2u.com.my',
     'classpath:scraper-configs/maybank-islamic.yml', true, NOW(), NOW())

ON CONFLICT (website_id) DO UPDATE SET
    website_name = EXCLUDED.website_name,
    base_url = EXCLUDED.base_url,
    config_path = EXCLUDED.config_path,
    updated_at = NOW();

-- Add comments
COMMENT ON TABLE scrape_sources IS 'Registry of websites configured for product scraping';
