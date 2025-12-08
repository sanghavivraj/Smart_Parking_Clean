package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Model.User;
import com.example.Smart_Parking.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // Show profile
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login"; // use your login page mapping
        }

        model.addAttribute("user", loggedUser);
        return "Profile"; // Profile.html
    }

    // Show Edit Profile Page
    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);
        return "EditProfile"; // Create EditProfile.html
    }

    // Handle Edit Profile Submission
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String phone,
                                @RequestParam String email,
                                HttpSession session,
                                Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        // Update User
        User updatedUser = userService.updateProfile(loggedUser.getId(), username, phone, email);

        // Update session with new user data
        session.setAttribute("loggedUser", updatedUser);

        model.addAttribute("success", true);
        model.addAttribute("user", updatedUser);

        return "Profile"; // Go back to profile page with updated info
    }
}
