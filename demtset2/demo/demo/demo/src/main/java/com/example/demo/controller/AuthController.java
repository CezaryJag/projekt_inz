package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required");
        }
        if (user.getSurname() == null || user.getSurname().isEmpty()) {
            return ResponseEntity.badRequest().body("Surname is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        // Save the user to the database
        userService.registerUser(
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getAddress(),
                user.getPhoneNumber()
        );
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        boolean authenticated = userService.authenticate(email, password);
        if (!authenticated) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User loggedInUser = userService.findByEmail(email);
        return ResponseEntity.ok(loggedInUser);
    }
}