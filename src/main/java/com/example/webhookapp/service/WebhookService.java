package com.example.webhookapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

import com.example.webhookapp.model.WebhookResponse;

@Service
public class WebhookService {

    private final WebClient webClient;

    @Value("${webhook.generate.url}")
    private String generateUrl;

    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

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

    public void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        Map<String, String> requestBody = Map.of("finalQuery", finalQuery);

        webClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
