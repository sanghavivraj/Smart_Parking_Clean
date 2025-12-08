package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Service.UserFeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserFeedbackController {

    private final UserFeedbackService service;

    public UserFeedbackController(UserFeedbackService service) {
        this.service = service;
    }

    @PostMapping("/saveFeedback")
    public String saveFeedback(@RequestParam String email,
                               @RequestParam String message) {

        service.save(email, message);

        return "redirect:/Home?feedbackSuccess=true";
    }
}
