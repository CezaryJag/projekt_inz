package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        userRepository.save(user);
        logger.info("User registered with email: {} and token: {}", user.getEmail(), token);

        String confirmationUrl = "http://localhost:8080/api/confirm?token=" + token;
        emailService.sendEmail(user.getEmail(), "Registration Confirmation", "Click the link to confirm your registration: " + confirmationUrl);

        return "Registration successful. Please check your email to confirm your registration.";
    }

    @GetMapping("/confirm")
    public String confirmUser(@RequestParam("token") String token) {
        Optional<User> userOptional = userRepository.findByConfirmationToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmailConfirmed(true);
            user.setConfirmationToken(null); // Clear the token to mark the user as confirmed
            userRepository.save(user);
            logger.info("User confirmed with email: {}", user.getEmail());
            return "Registration confirmed. You can now log in.";
        } else {
            logger.warn("Invalid confirmation token: {}", token);
            return "Invalid confirmation token.";
        }
    }
}