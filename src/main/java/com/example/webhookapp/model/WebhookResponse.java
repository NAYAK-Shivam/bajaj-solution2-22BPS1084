package com.example.webhookapp.model;
public class WebhookResponse {
    private String status;
    private String message;
    private Data data;

    public static class Data {
        private String webhook;
        private String accessToken;

        public String getWebhook() { return webhook; }
        public void setWebhook(String webhook) { this.webhook = webhook; }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
