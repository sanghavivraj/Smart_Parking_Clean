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
        System.out.println("SMTP DEBUG USERNAME = " + System.getenv("SPRING_MAIL_USERNAME"));
        System.out.println("SMTP DEBUG PASSWORD = " +
                (System.getenv("SPRING_MAIL_PASSWORD") != null ? "LOADED" : "NULL"));

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Smart Parking Verification Code");
        msg.setText("Your OTP is: " + otp + "\nThis code is valid for 10 minutes.");

        mailSender.send(msg);
    }



}
