package com.example.Smart_Parking.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${MAILTRAP_API_TOKEN}")
    private String apiToken;

    @Value("${MAILTRAP_INBOX_ID}")
    private String inboxId;

    public void sendVerificationEmail(String toEmail, String otp) {

        String body = """
        {
            "to": ["%s"],
            "subject": "Smart Parking Verification Code",
            "text": "Your OTP is: %s"
        }
        """.formatted(toEmail, otp);

        String url = "https://sandbox.api.mailtrap.io/api/send/" + inboxId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Api-Token", apiToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("MAILTRAP API STATUS: " + response.statusCode());
            System.out.println("MAILTRAP API RESPONSE: " + response.body());

        } catch (Exception e) {
            throw new RuntimeException("Mailtrap API failed", e);
        }
    }
}
