package com.adityaraj.neurochat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class GeminiService {

	@Value("${OPENROUTER_API_KEY}")
    private String apiKey;

    private final String URL = "https://openrouter.ai/api/v1/chat/completions";

    public String getResponse(String userInput) {

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();

        request.put("model", "openai/gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userInput);

        messages.add(userMessage);

        request.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.add("HTTP-Referer", "http://localhost:8080");
        headers.add("X-Title", "NeuroChat");


        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            List choices = (List) response.getBody().get("choices");

            if (choices == null || choices.isEmpty()) {
                return "No response from AI";
            }

            Map first = (Map) choices.get(0);
            Map message = (Map) first.get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}