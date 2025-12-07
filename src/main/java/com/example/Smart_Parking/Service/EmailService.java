package com.example.Smart_Parking.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Smart Parking Email Verification");
        message.setText(
                "Your OTP is: " + otp +
                        "\n\nThis code is valid for 10 minutes."
        );

        mailSender.send(message);

        System.out.println("SMTP EMAIL SENT TO: " + toEmail);
    }
}
