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
        return ResponseEntity.ok("User registered successfully, check your email");
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
        String token = jwtService.generateToken(loggedInUser.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", loggedInUser);
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