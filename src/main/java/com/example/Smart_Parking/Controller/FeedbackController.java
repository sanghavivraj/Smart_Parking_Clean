package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Model.Feedback;
import com.example.Smart_Parking.Service.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping("/feedback-submit")
    public String submitFeedback(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String subject,
            @RequestParam String message,
            Model model
    ) {
        Feedback fb = new Feedback();
        fb.setName(name);
        fb.setEmail(email);
        fb.setSubject(subject);
        fb.setMessage(message);

        service.save(fb);

        model.addAttribute("success", true);
        return "ContactUs";   // reload page with success popup
    }
}
