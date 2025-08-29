package com.example.webhookapp.model;

public class WebhookResponse {
    private String status;
    private String message;

    // Constructor
    public WebhookResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
