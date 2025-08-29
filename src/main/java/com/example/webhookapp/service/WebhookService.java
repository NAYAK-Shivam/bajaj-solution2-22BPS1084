package com.example.webhookapp.service;

import com.example.webhookapp.entity.Solution;
import com.example.webhookapp.model.GenerateWebhookRequest;
import com.example.webhookapp.model.GenerateWebhookResponse;
import com.example.webhookapp.repo.SolutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.annotation.PostConstruct;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class WebhookService {

    private final WebClient baseWebClient;
    private final SolutionRepository repository;

    @Value("${app.name}")
    private String name;

    @Value("${app.regNo}")
    private String regNo;

    @Value("${app.email}")
    private String email;

    public WebhookService(WebClient baseWebClient, SolutionRepository repository) {
        this.baseWebClient = baseWebClient;
        this.repository = repository;
    }

    @PostConstruct
    public void startFlow() {
        // run in a new thread so startup isn't blocked too long (optional)
        new Thread(this::performFlow).start();
    }

    public void performFlow() {
        try {
            // 1) Call generateWebhook
            GenerateWebhookRequest req = new GenerateWebhookRequest(name, regNo, email);
            GenerateWebhookResponse resp = baseWebClient.post()
                    .uri("/hiring/generateWebhook/JAVA")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(GenerateWebhookResponse.class)
                    .block();

            if (resp == null || resp.getWebhook() == null || resp.getAccessToken() == null) {
                System.err.println("Failed to get webhook/accessToken. Response: " + resp);
                return;
            }

            String webhookUrl = resp.getWebhook();
            String accessToken = resp.getAccessToken();

            // 2) Determine question based on last 2 digits of regNo
            int lastTwo = extractLastTwoDigits(regNo);
            boolean isEven = (lastTwo % 2 == 0);

            // 3) Choose final query (question 2 since your regNo is even)
            String finalQuery;
            if (isEven) {
                finalQuery = getQuestion2Sql();
            } else {
                finalQuery = "-- Question 1 SQL not implemented in this demo";
            }

            // 4) store result to DB
            Solution s = new Solution();
            s.setRegNo(regNo);
            s.setFinalQuery(finalQuery);
            s.setCreatedAt(LocalDateTime.now());
            repository.save(s);

            // 5) write to file
            Path outDir = Paths.get("output");
            Files.createDirectories(outDir);
            Files.writeString(outDir.resolve("finalQuery.sql"), finalQuery, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // 6) Submit finalQuery JSON to webhook url using Authorization header
            WebClient clientToUse = webhookUrl.startsWith("http") ? WebClient.create() : baseWebClient;
            Map<String, String> body = Map.of("finalQuery", finalQuery);

            String result = clientToUse.post()
                    .uri(webhookUrl)
                    .header("Authorization", accessToken)   // NOTE: header value is the raw accessToken, not "Bearer ..."
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Submitted finalQuery; response=" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int extractLastTwoDigits(String regNo) {
        String digitsOnly = regNo.replaceAll("\\D+", "");
        if (digitsOnly.length() == 0) return 0;
        if (digitsOnly.length() == 1) return Integer.parseInt(digitsOnly);
        String lastTwo = digitsOnly.substring(digitsOnly.length() - 2);
        return Integer.parseInt(lastTwo);
    }

    private static String getQuestion2Sql() {
        return """
                SELECT e.emp_id,
                       e.first_name,
                       e.last_name,
                       d.department_name,
                       COUNT(e2.emp_id) AS younger_employees_count
                FROM employee e
                JOIN department d ON e.department = d.department_id
                LEFT JOIN employee e2 ON e2.department = e.department AND e2.dob > e.dob
                GROUP BY e.emp_id, e.first_name, e.last_name, d.department_name
                ORDER BY e.emp_id DESC;
                """;
    }
}
