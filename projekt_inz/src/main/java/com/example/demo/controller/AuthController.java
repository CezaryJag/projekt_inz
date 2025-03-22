package com.example.demo.controller;

import com.example.demo.entity.ResetPasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService) {
        this.userService = userService;
        this.jwtService = new JwtService();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }
        if (userService.emailExists(user.getEmail())) {
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty() || !isValidPassword(user.getPassword())) {
            response.put("message", "Password must be at least 6 characters long and contain at least one uppercase letter");
            return ResponseEntity.badRequest().body(response);
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().length() != 9) {
            response.put("message", "Phone number must be 9 digits long");
            return ResponseEntity.badRequest().body(response);
        }
        if (!user.getPhoneNumber().matches("\\d{9}")) {
            response.put("message", "Phone number must contain only digits");
            return ResponseEntity.badRequest().body(response);
        }
        if (userService.phoneNumberExists(user.getPhoneNumber())) {
            response.put("message", "Phone number already exists");
            return ResponseEntity.badRequest().body(response);
        }

        userService.registerUser(
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getAddress(),
                user.getPhoneNumber()
        );
        response.put("message", "User registered successfully, check your email");
        return ResponseEntity.ok(response);
    }

    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 6 && password.chars().anyMatch(Character::isUpperCase);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam String email, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();
        if (email == null || email.isEmpty()) {
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }
        if (password == null || password.isEmpty()) {
            response.put("message", "Password is required");
            return ResponseEntity.badRequest().body(response);
        }

        boolean authenticated = userService.authenticate(email, password);
        if (!authenticated) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        User loggedInUser = userService.findByEmail(email);
        String token = jwtService.generateToken(loggedInUser.getEmail(),loggedInUser.getRole());
        response.put("token", token);
        response.put("name", loggedInUser.getName());
        response.put("surname", loggedInUser.getSurname());
        response.put("id", String.valueOf(loggedInUser.getUserId()));
        response.put("role", loggedInUser.getRole());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String token) {
        System.out.println("Otrzymano żądanie z tokenem: " + token);
        boolean result = userService.confirmToken(token);
        if (result) {
            return ResponseEntity.ok("Email successfully confirmed.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("AUTH CONTROLLER DZIALA");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is required");
            }

            String jwtToken = token.substring(7);  // Usunięcie "Bearer " z tokenu
            Claims claims = jwtService.extractAllClaims(jwtToken);
            if (claims == null || jwtService.isTokenExpired(jwtToken)) {
                System.out.println("AUTH CONTROLLER NIE DZIALA");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or expired");
            }

            return ResponseEntity.ok("Token is valid");
        } catch (Exception e) {
            System.out.println("AUTH CONTROLLER CATCH");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        boolean emailSent = userService.initiatePasswordReset(email);
        if (emailSent) {
            return ResponseEntity.ok("E-mail do resetowania hasła został wysłany.");
        } else {
            return ResponseEntity.badRequest().body("Nie znaleziono użytkownika z podanym adresem e-mail.");
        }
    }

    @GetMapping("/reset-password")
    public void resetPasswordForm(@RequestParam String token, HttpServletResponse response) throws IOException, java.io.IOException {
        if (userService.validateResetToken(token)) {
            // Przekierowanie wraz z tokenem do strony HTML
            response.sendRedirect("/resetPasswordHTML.html?token=" + token);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token jest nieprawidłowy lub wygasł.");
        }
    }

    @PostMapping("/set-new-password")
    public ResponseEntity<?> setNewPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        boolean passwordReset = userService.resetPassword(token, newPassword);
        if (passwordReset) {
            return ResponseEntity.ok("Hasło zostało zresetowane.");
        } else {
            return ResponseEntity.badRequest().body("Token jest nieprawidłowy lub wygasł.");
        }
    }

}