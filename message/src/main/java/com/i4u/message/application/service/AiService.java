package com.i4u.message.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public AiService(@Value("${gemini.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public String generateResponse(String prompt) {
        String url = GEMINI_API_URL + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("text", prompt);

        Map<String, Object> part = new HashMap<>();
        part.put("parts", List.of(textContent));

        requestBody.put("contents", List.of(part));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // JSON 응답에서 텍스트 추출
                return extractTextFromResponse(response.getBody());
            }
            return "응답을 받지 못했습니다.";
        } catch (Exception e) {
            return "API 요청 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(Map responseBody) {
        try {
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map candidate = candidates.get(0);
                Map content = (Map) candidate.get("content");
                List<Map> parts = (List<Map>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
            return "응답에서 텍스트를 추출할 수 없습니다.";
        } catch (Exception e) {
            return "응답 처리 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    public String askQuestion(String question) {
        return generateResponse(question);
    }
}