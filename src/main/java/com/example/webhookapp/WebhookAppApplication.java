package com.example.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.webhookapp.service.WebhookService;
import com.example.webhookapp.model.WebhookResponse;

@SpringBootApplication
public class WebhookAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookAppApplication.class, args);
    }

    @Bean
    CommandLineRunner run(WebhookService webhookService) {
        return args -> {
            // 1. Generate webhook
            WebhookResponse response = webhookService.generateWebhook();

            if (response == null || response.getData() == null) {
                System.out.println("Failed to generate webhook!");
                return;
            }

            String webhookUrl = response.getData().getWebhook();
            String accessToken = response.getData().getAccessToken();

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            // 2. Solve SQL (regNo = 22 → even → Question 2)
            String finalQuery =
                "SELECT e.emp_id, e.first_name, e.last_name, d.department_name, " +
                "COUNT(e2.emp_id) AS younger_employees_count " +
                "FROM employee e " +
                "JOIN department d ON e.department = d.department_id " +
                "LEFT JOIN employee e2 ON e2.department = e.department AND e2.dob > e.dob " +
                "GROUP BY e.emp_id, e.first_name, e.last_name, d.department_name " +
                "ORDER BY e.emp_id DESC;";

            // 3. Submit solution
            webhookService.submitSolution(webhookUrl, accessToken, finalQuery);

            System.out.println("Final query submitted successfully!");
        };
    }
}
