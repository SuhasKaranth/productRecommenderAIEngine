package com.smartguide.poc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for LLM providers
 */
@Configuration
@ConfigurationProperties(prefix = "app.llm")
@Data
public class LLMConfig {

    private String provider = "ollama";

    private AzureConfig azure = new AzureConfig();
    private OllamaConfig ollama = new OllamaConfig();

    @Data
    public static class AzureConfig {
        private String endpoint;
        private String apiKey;
        private String deploymentName;
        private String apiVersion = "2024-02-15-preview";
    }

    @Data
    public static class OllamaConfig {
        private String host = "http://localhost:11434";
        private String model = "llama3.2";
        private Integer timeout = 30000;
    }
}
