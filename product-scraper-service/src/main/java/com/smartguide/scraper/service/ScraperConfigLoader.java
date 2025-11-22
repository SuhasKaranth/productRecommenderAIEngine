package com.smartguide.scraper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.smartguide.scraper.model.ScraperConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to load and manage scraper configurations from YAML files
 */
@Service
@Slf4j
public class ScraperConfigLoader {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper yamlMapper;
    private final Map<String, ScraperConfig> configCache;

    public ScraperConfigLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.configCache = new HashMap<>();
        loadAllConfigs();
    }

    /**
     * Load all scraper configurations from classpath:scraper-configs/
     */
    public void loadAllConfigs() {
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:scraper-configs/*.yml");

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    ScraperConfig config = yamlMapper.readValue(inputStream, ScraperConfig.class);
                    configCache.put(config.getWebsiteId(), config);
                    log.info("Loaded scraper config for website: {}", config.getWebsiteId());
                } catch (IOException e) {
                    log.error("Failed to load config from: {}", resource.getFilename(), e);
                }
            }

            log.info("Loaded {} scraper configurations", configCache.size());
        } catch (IOException e) {
            log.error("Failed to load scraper configurations", e);
        }
    }

    /**
     * Get configuration for a specific website
     */
    public ScraperConfig getConfig(String websiteId) {
        ScraperConfig config = configCache.get(websiteId);
        if (config == null) {
            log.warn("No configuration found for website: {}", websiteId);
        }
        return config;
    }

    /**
     * Load configuration from a specific file path
     */
    public ScraperConfig loadConfigFromFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            ScraperConfig config = yamlMapper.readValue(inputStream, ScraperConfig.class);
            configCache.put(config.getWebsiteId(), config);
            log.info("Loaded scraper config from file: {}", filePath);
            return config;
        }
    }

    /**
     * Get all loaded configurations
     */
    public Map<String, ScraperConfig> getAllConfigs() {
        return new HashMap<>(configCache);
    }

    /**
     * Reload all configurations
     */
    public void reloadConfigs() {
        configCache.clear();
        loadAllConfigs();
    }
}
