package com.example.Smart_Parking.Model;

import jakarta.persistence.*;

@Entity
public class UserFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(length = 2000)
    private String message;

    public UserFeedback() {}

    public UserFeedback(String email, String message) {
        this.email = email;
        this.message = message;
    }

    // getters and setters
}
