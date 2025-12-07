package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.Model.User;
import com.example.Smart_Parking.Service.EmailService;
import com.example.Smart_Parking.Service.UserService;
import com.example.Smart_Parking.Service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationService verificationService;

    public RegisterController(UserService userService,
                              EmailService emailService,
                              VerificationService verificationService) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationService = verificationService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userForm", new User());
        return "Register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("userForm") User userForm,
                                 BindingResult br,
                                 Model model) {

        if (br.hasErrors()) return "Register";

        boolean ok = userService.register(userForm);

        if (!ok) {
            model.addAttribute("error", "Email already exists!");
            return "Register";
        }

        // Generate OTP
        String token = verificationService.generateToken(userForm.getEmail());

        // Send email via SMTP
        emailService.sendVerificationEmail(userForm.getEmail(), token);

        model.addAttribute("email", userForm.getEmail());
        return "VerifyEmail";
    }

    @PostMapping("/verify-email-submit")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String token,
                              Model model) {

        boolean valid = verificationService.verifyToken(email, token);

        if (!valid) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP.");
            return "VerifyEmail";
        }

        verificationService.invalidate(email);
        userService.markEmailVerified(email);

        return "redirect:/Userlogin";
    }
}
