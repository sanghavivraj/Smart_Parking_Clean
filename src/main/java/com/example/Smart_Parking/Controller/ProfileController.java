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

    // ---------------- SHOW PROFILE ----------------
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);
        return "Profile";
    }

    // ---------------- SHOW EDIT PAGE ----------------
    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedUser);
        return "EditProfile"; // must match EditProfile.html
    }

    // ---------------- HANDLE UPDATE ----------------
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("username") String username,
                                @RequestParam("phone") String phone,
                                @RequestParam("email") String email,
                                HttpSession session,
                                Model model) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        // Call service to update user in database
        User updatedUser = userService.updateProfile(
                loggedUser.getUserid(),
                username,
                phone,
                email
        );

        // Update session
        session.setAttribute("loggedUser", updatedUser);

        model.addAttribute("user", updatedUser);
        model.addAttribute("success", "Profile updated successfully!");

        return "redirect:/profile";
    }
}
