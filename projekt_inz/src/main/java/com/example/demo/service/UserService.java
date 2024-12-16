package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.VerificationToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public User registerUser(String name, String surname, String email, String password, String role, String address, String phoneNumber) {
        // Sprawdzenie, czy e-mail jest już zajęty
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Szyfrowanie hasła
        String encodedPassword = passwordEncoder.encode(password);

        // Tworzenie obiektu User
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setAccountCreationDate(new Date());
        user.setEmailConfirmed(false); // Ustawienie jako niepotwierdzone

        // Zapis użytkownika w bazie danych
        userRepository.save(user);

        // Generowanie tokenu weryfikacyjnego
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(1); // Token ważny przez 24 godziny
        tokenRepository.save(token);

        // Wysyłanie e-maila z linkiem weryfikacyjnym
        sendVerificationEmail(email, token.getToken());

        return user;
    }

    private void sendVerificationEmail(String email, String token) {
        String subject = "Potwierdź swoją rejestrację";
        String confirmationUrl = "http://localhost:8080/api/auth/confirm?token=" + token;
        String message = "Kliknij w poniższy link, aby aktywować konto:\n" + confirmationUrl;

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        mailSender.send(emailMessage);
    }

    public boolean confirmToken(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isPresent()) {
            System.out.println("Token znaleziony: " + token);

            if (!optionalToken.get().isExpired()) {
                VerificationToken verificationToken = optionalToken.get();
                User user = verificationToken.getUser();
                user.setEnabled(true);
                userRepository.save(user);
                System.out.println("Token usuwany: " + verificationToken.getToken());
                tokenRepository.delete(verificationToken);
                return true;
            } else {
                System.out.println("Token wygasł: " + token);
            }
        } else {
            System.out.println("Nie znaleziono tokenu: " + token);
        }
        return false;
    }

    public boolean authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    public void removeExpiredUnverifiedAccounts() {
        // Pobierz wszystkie nieweryfikowane konta
        List<VerificationToken> expiredTokens = tokenRepository.findAll().stream()
                .filter(VerificationToken::isExpired) // Sprawdzenie czy token wygasł
                .toList();

        for (VerificationToken token : expiredTokens) {
            User user = token.getUser();
            System.out.println("Usunięto konto użytkownika: " + user.getEmail());
            // Usuń token i użytkownika
            tokenRepository.delete(token);
            userRepository.delete(user);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}