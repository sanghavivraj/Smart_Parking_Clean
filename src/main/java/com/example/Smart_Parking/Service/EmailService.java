package com.example.Smart_Parking.Service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class EmailService {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void sendVerificationEmail(String toEmail, String token) {
        String apiKey = System.getenv("BREVO_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("BREVO_API_KEY not set");
        }

        String domain = System.getenv("APP_DOMAIN");
        if (domain == null || domain.isEmpty()) {
            domain = "localhost:8080";
        }

        String verifyUrl = "https://" + domain + "/verify-email-submit?email=" + toEmail + "&token=" + token;

        String json = "{"
                + "\"sender\":{\"email\":\"noreply@smartparking.com\",\"name\":\"Smart Parking\"},"
                + "\"to\":[{\"email\":\"" + toEmail + "\"}],"
                + "\"subject\":\"Smart Parking - Verify Email\","
                + "\"htmlContent\":\"<p>Your verification code is: <b>" + token + "</b></p>"
                + "<p><a href='" + verifyUrl + "'>Verify</a></p>\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("BREVO API STATUS = " + resp.statusCode());
            System.out.println("BREVO RESPONSE = " + resp.body());
        } catch (Exception e) {
            throw new RuntimeException("Email failed: " + e.getMessage());
        }
    }
}
