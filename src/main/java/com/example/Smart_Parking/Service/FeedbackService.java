package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.Feedback;
import com.example.Smart_Parking.Repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    private final FeedbackRepository repo;

    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }

    public void save(Feedback feedback) {
        feedback.setCreatedAt(LocalDateTime.now());
        repo.save(feedback);
    }
}
