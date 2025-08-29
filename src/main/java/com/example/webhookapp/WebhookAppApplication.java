package com.example.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.webhookapp.service.WebhookService;
import com.example.webhookapp.model.WebhookResponse;

@SpringBootApplication
public class WebhookAppApplication implements CommandLineRunner {

    private final WebhookService webhookService;

    public WebhookAppApplication(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebhookAppApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Step 1: Generate webhook & token
        WebhookResponse response = webhookService.generateWebhook();
        System.out.println("Webhook URL: " + response.getWebhook());
        System.out.println("Access Token: " + response.getAccessToken());

        // Step 2: Submit SQL solution immediately
        String finalQuery = " SELECT e.emp_id, e.first_name, e.last_name, d.department_name,COUNT(e2.emp_id) AS younger_employees_count FROM employee e JOIN department d ON e.department = d.department_id LEFT JOIN employee e2 ON e2.department = e.department AND e2.dob > e.dob GROUP BY e.emp_id, e.first_name, e.last_name, d.department_name ORDER BY e.emp_id DESC; ";
        webhookService.submitSolution(response.getWebhook(), response.getAccessToken(), finalQuery);
    }
}
