package com.example.webhookapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import com.example.webhookapp.model.WebhookResponse;
import java.util.Map;

@Service
public class WebhookService {

    private final WebClient webClient;

    @Value("${webhook.generate.url}")
    private String generateUrl;

    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // Step 1: Generate webhook & access token
    public WebhookResponse generateWebhook() {
        Map<String, String> requestBody = Map.of(
                "name", "Shivam Nayak",
                "regNo", "22BPS1084",
                "email", "shivam.nayak2022@vitstudent.ac.in"
        );

        return webClient.post()
                .uri(generateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(WebhookResponse.class)
                .block();
    }

    // Step 2: Submit SQL solution using webhook & access token
    public void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        Map<String, String> requestBody = Map.of("finalQuery", finalQuery);

        try {
            webClient.post()
    .uri(webhookUrl)
    .contentType(MediaType.APPLICATION_JSON)
    .headers(headers -> headers.set("Authorization", accessToken)) // no "Bearer "
    .bodyValue(Map.of("finalQuery", finalQuery))
    .retrieve()
    .bodyToMono(String.class)
    .block();
            System.out.println("Final query submitted successfully!");
        } catch (Exception e) {
            System.out.println("Failed to submit solution!");
            e.printStackTrace();
        }
    }
}
