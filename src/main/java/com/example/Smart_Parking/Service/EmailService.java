package com.example.Smart_Parking.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {

        // Get deployed domain from Railway variables
        String domain = System.getenv("APP_DOMAIN");
        if (domain == null || domain.isEmpty()) {
            domain = "localhost:8080"; // fallback (local)
        }

        String verifyUrl =
                "https://" + domain + "/verify-email-submit?email=" + toEmail + "&token=" + token;

        String subject = "Smart Parking - Verify Your Email";

        String text = "Your verification code is: " + token +
                "\n\nClick to verify (recommended):\n" + verifyUrl +
                "\n\nThis code expires in 10 minutes.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            System.out.println("Verification email sent to " + toEmail);

        } catch (Exception e) {
            System.out.println("EMAIL ERROR: " + e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
