package com.smartguide.poc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartguide.poc.config.LLMConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * Service for extracting intent from user input using LLM
 * Supports both Azure OpenAI and Ollama
 */
@Service
@Slf4j
public class LLMService {

    private static final String SYSTEM_PROMPT = """
            You are a banking assistant specializing in Islamic finance products.
            Extract the customer's intent from their input and return a JSON response.

            Valid intents:
            - TRAVEL: Travel-related needs (cards, insurance)
            - LOAN: General financing or loan requests
            - SAVINGS: Savings accounts or deposit products
            - INVESTMENT: Investment products or wealth management
            - INSURANCE: Insurance or Takaful products
            - CAR: Auto financing or car-related products
            - HOME: Home financing or property-related products
            - EDUCATION: Education financing
            - BUSINESS: Business banking or SME products
            - PAYMENT: Payment solutions or cards
            - GENERAL: General inquiry or unclear intent

            Response format:
            {
                "intent": "INTENT_NAME",
                "confidence": 0.0-1.0,
                "entities": {
                    "key": "value"
                }
            }

            Extract relevant entities like destination, amount, duration, etc.
            If the intent is unclear, use "GENERAL" with lower confidence.
            """;

    private final LLMConfig llmConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public LLMService(LLMConfig llmConfig, ObjectMapper objectMapper) {
        this.llmConfig = llmConfig;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().build();
    }

    /**
     * Extract intent from user input
     */
    public Map<String, Object> extractIntent(String userInput, String language) {
        try {
            if ("azure".equalsIgnoreCase(llmConfig.getProvider())) {
                return extractIntentAzure(userInput, language);
            } else if ("ollama".equalsIgnoreCase(llmConfig.getProvider())) {
                return extractIntentOllama(userInput, language);
            } else {
                throw new IllegalArgumentException("Unknown LLM provider: " + llmConfig.getProvider());
            }
        } catch (Exception e) {
            log.error("LLM error: {}, using fallback", e.getMessage());
            return getFallbackIntent(userInput);
        }
    }

    /**
     * Extract intent using Azure OpenAI
     */
    private Map<String, Object> extractIntentAzure(String userInput, String language) {
        String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=%s",
                llmConfig.getAzure().getEndpoint(),
                llmConfig.getAzure().getDeploymentName(),
                llmConfig.getAzure().getApiVersion());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", String.format("Extract intent from this %s text: %s", language, userInput))
        ));
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 200);
        requestBody.put("response_format", Map.of("type", "json_object"));

        try {
            String response = webClient.post()
                    .uri(url)
                    .header("api-key", llmConfig.getAzure().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            return parseAzureResponse(response);
        } catch (Exception e) {
            log.error("Azure OpenAI error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extract intent using Ollama
     */
    private Map<String, Object> extractIntentOllama(String userInput, String language) {
        String url = llmConfig.getOllama().getHost() + "/api/generate";
        String prompt = String.format("%s\n\nExtract intent from this %s text: %s",
                SYSTEM_PROMPT, language, userInput);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", llmConfig.getOllama().getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("format", "json");
        requestBody.put("stream", false);
        requestBody.put("options", Map.of(
                "temperature", 0.3,
                "num_predict", 200
        ));

        try {
            String response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(llmConfig.getOllama().getTimeout()))
                    .block();

            return parseOllamaResponse(response);
        } catch (Exception e) {
            log.error("Ollama error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Parse Azure OpenAI response
     */
    private Map<String, Object> parseAzureResponse(String response) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();
        JsonNode intentData = objectMapper.readTree(content);
        return validateIntentResponse(intentData);
    }

    /**
     * Parse Ollama response
     */
    private Map<String, Object> parseOllamaResponse(String response) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(response);
        String content = root.path("response").asText();
        JsonNode intentData = objectMapper.readTree(content);
        return validateIntentResponse(intentData);
    }

    /**
     * Validate and normalize LLM response
     */
    private Map<String, Object> validateIntentResponse(JsonNode response) {
        Set<String> validIntents = Set.of(
                "TRAVEL", "LOAN", "SAVINGS", "INVESTMENT", "INSURANCE",
                "CAR", "HOME", "EDUCATION", "BUSINESS", "PAYMENT", "GENERAL"
        );

        String intent = response.path("intent").asText("GENERAL").toUpperCase();
        double confidence = response.path("confidence").asDouble(0.5);
        Map<String, Object> entities = new HashMap<>();

        // Parse entities if present
        if (response.has("entities")) {
            JsonNode entitiesNode = response.get("entities");
            entitiesNode.fields().forEachRemaining(entry ->
                entities.put(entry.getKey(), entry.getValue().asText())
            );
        }

        // Validate intent
        if (!validIntents.contains(intent)) {
            intent = "GENERAL";
            confidence = Math.min(confidence, 0.5);
        }

        // Clamp confidence between 0 and 1
        confidence = Math.max(0.0, Math.min(1.0, confidence));

        Map<String, Object> result = new HashMap<>();
        result.put("intent", intent);
        result.put("confidence", confidence);
        result.put("entities", entities);

        return result;
    }

    /**
     * Get fallback intent when LLM fails
     */
    private Map<String, Object> getFallbackIntent(String userInput) {
        String inputLower = userInput.toLowerCase();

        Map<String, List<String>> intentKeywords = Map.of(
                "TRAVEL", Arrays.asList("travel", "trip", "vacation", "flight", "hotel"),
                "LOAN", Arrays.asList("loan", "finance", "borrow", "credit"),
                "SAVINGS", Arrays.asList("save", "savings", "account", "deposit"),
                "INVESTMENT", Arrays.asList("invest", "fund", "portfolio", "wealth"),
                "INSURANCE", Arrays.asList("insurance", "takaful", "protect", "coverage"),
                "CAR", Arrays.asList("car", "auto", "vehicle", "drive"),
                "HOME", Arrays.asList("home", "house", "property", "mortgage"),
                "EDUCATION", Arrays.asList("education", "study", "university", "school"),
                "BUSINESS", Arrays.asList("business", "company", "sme", "corporate"),
                "PAYMENT", Arrays.asList("payment", "card", "pay", "transaction")
        );

        for (Map.Entry<String, List<String>> entry : intentKeywords.entrySet()) {
            if (entry.getValue().stream().anyMatch(inputLower::contains)) {
                Map<String, Object> result = new HashMap<>();
                result.put("intent", entry.getKey());
                result.put("confidence", 0.6);
                result.put("entities", new HashMap<>());
                return result;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("intent", "GENERAL");
        result.put("confidence", 0.3);
        result.put("entities", new HashMap<>());
        return result;
    }
}
