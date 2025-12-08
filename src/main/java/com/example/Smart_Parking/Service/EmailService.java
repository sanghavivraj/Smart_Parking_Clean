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

        // Debug: Check environment variables
        System.out.println("ENV SPRING_MAIL_USERNAME = " + System.getenv("SPRING_MAIL_USERNAME"));
        System.out.println("ENV SPRING_MAIL_PASSWORD = " +
                (System.getenv("SPRING_MAIL_PASSWORD") != null ? "LOADED" : "NULL"));

        // Debug: Check Spring mail properties (JavaMail uses these)
        System.out.println("SYS spring.mail.username = " + System.getProperty("spring.mail.username"));
        System.out.println("SYS spring.mail.password = " +
                (System.getProperty("spring.mail.password") != null ? "LOADED" : "NULL"));

        // Create the email message
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Your Smart Parking OTP");
        msg.setText("Your OTP for email verification is: " + otp + "\nThis code is valid for 10 minutes.");

        // Now send
        try {
            mailSender.send(msg);
            System.out.println("SMTP EMAIL SENT SUCCESSFULLY TO → " + toEmail);
        } catch (Exception e) {
            System.out.println("SMTP SENDING FAILED: " + e.getMessage());
            throw e;
        }
    }
}
