package com.example.Smart_Parking.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        Object loggedUser = session.getAttribute("loggedUser"); // MATCH THIS

        if (loggedUser == null) {
            return "redirect:/Userlogin"; // FIX THIS based on your login mapping
        }

        model.addAttribute("user", loggedUser);

        return "Profile"; // MATCHES Profile.html
    }
}
