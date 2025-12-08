package com.example.Smart_Parking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrivacyController {

    @GetMapping("/Terms")
    public String terms() {
        return "Terms";
    }

    @GetMapping("/Privacy")
    public String privacy() {
        return "Privacy";
    }
}