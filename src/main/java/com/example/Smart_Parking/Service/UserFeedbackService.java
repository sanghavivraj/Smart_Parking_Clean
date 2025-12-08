package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.UserFeedback;
import com.example.Smart_Parking.Repository.UserFeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class UserFeedbackService {

    private final UserFeedbackRepository repo;

    public UserFeedbackService(UserFeedbackRepository repo) {
        this.repo = repo;
    }

    public void save(String email, String message) {
        repo.save(new UserFeedback(email, message));
    }

}
