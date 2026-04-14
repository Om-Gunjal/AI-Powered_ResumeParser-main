package com.example.resumeparser.service;

import com.example.resumeparser.model.ParsedResumeData;
import com.example.resumeparser.model.RoleFitness;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public ParsedResumeData parseResumeWithOpenAi(String resumeText) {

        // 1) Build the request JSON for "messages" array
        String requestBody = buildRequestJson(resumeText);

        // 2) Use RestTemplate to POST
        RestTemplate restTemplate = new RestTemplate();

        // Create headers
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openAiApiKey);

        // Wrap body + headers
        org.springframework.http.HttpEntity<String> entity =
                new org.springframework.http.HttpEntity<>(requestBody, headers);

        // 3) Send request
        org.springframework.http.ResponseEntity<String> response =
                restTemplate.postForEntity(OPENAI_URL, entity, String.class);

        // 4) Parse the response JSON
        String jsonResponse = response.getBody();
        // System.out.println("DEBUG >> OpenAI raw JSON = " + jsonResponse);

        // 5) Extract the content from that JSON
        String assistantContent = extractAssistantContent(jsonResponse);

        // 6) Parse that bracketed string into our POJO
        ParsedResumeData parsed = parseBracketedString(assistantContent);

        return parsed;
    }

    private String buildRequestJson(String resumeText) {

        String sanitized = escapeForJson(resumeText);

        return """
            {
              "model": "gpt-4o-mini",
              "messages": [
                {
                  "role": "developer",
                  "content": "I will provide you with the raw text of a resume, and I want you to parse it and extract the following information and always respond only in this format: [firstname: , lastname: , email: , suggested_role_1: , fitness_role_1: , suggested_role_2: , fitness_role_2: , suggested_role_3: , fitness_role_3: ]. Recommend maximum of 3 roles for the given resume, and score the fitness of the resume for each recommended role with an integer between 1 to 5. You can return null for recommended roles if you see no match."
                },
                {
                  "role": "user",
                  "content": "Here is the resume summary:\\n\\n[%s]"
                }
              ],
              "temperature": 0.5,
              "max_tokens": 150
            }
            """.formatted(sanitized);
    }

    private String escapeForJson(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String extractAssistantContent(String json) {
        int index = json.indexOf("\"content\":");
        int start = json.indexOf("\"", index + 10) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private ParsedResumeData parseBracketedString(String bracketed) {
        ParsedResumeData result = new ParsedResumeData();
        List<RoleFitness> roleFitnessList = new ArrayList<>();

        result.setFirstname(findField(bracketed, "firstname:"));
        result.setLastname(findField(bracketed, "lastname:"));
        result.setEmail(findField(bracketed, "email:"));

        for (int i = 1; i <= 3; i++) {
            String role = findField(bracketed, "suggested_role_" + i + ":");
            String fit = findField(bracketed, "fitness_role_" + i + ":");

            if (role != null && fit != null) {
                roleFitnessList.add(new RoleFitness(role, Integer.parseInt(fit)));
            }
        }

        result.setRoleFitnessList(roleFitnessList);
        return result;
    }


    private String findField(String input, String fieldName) {
        int start = input.indexOf(fieldName);
        if (start == -1) {
            return null;
        }
        start += fieldName.length();

        while (start < input.length() && input.charAt(start) == ' ') {
            start++;
        }
        int end = input.indexOf(",", start);
        if (end == -1) {
            // Look for closing bracket if it's the last field.
            end = input.indexOf("]", start);
        }
        if (end == -1) {
            end = input.length();
        }
        return input.substring(start, end).trim();
    }
}