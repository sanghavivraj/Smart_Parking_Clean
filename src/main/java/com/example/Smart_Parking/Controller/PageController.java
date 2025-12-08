package com.example.Smart_Parking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/ContactUs")
    public String contactUs() {
        return "ContactUs";   // This must match ContactUs.html (without .html)
    }
}

