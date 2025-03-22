package com.example.demo.service;

import com.example.demo.entity.AccessKey;
import com.example.demo.entity.ResetToken;
import com.example.demo.entity.User;
import com.example.demo.entity.VerificationToken;
import com.example.demo.repository.AccessKeyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.repository.ResetTokenRepository;
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
    private final ResetTokenRepository resetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccessKeyRepository accessKeyRepository;


    public boolean isValidAccessKey(String accessKey) {
        Optional<AccessKey> key = accessKeyRepository.findByKeyValueAndIsUsedFalse(accessKey);
        return key.isPresent();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository tokenRepository, ResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.resetTokenRepository = resetTokenRepository;
    }

    public User registerUser(String name, String surname, String email, String password, String role, String address, String phoneNumber,String accessKey) {
        // Sprawdzenie, czy e-mail jest już zajęty
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if (role == null || role.isEmpty()) {
            role = "USER";
        }
        AccessKey key = accessKeyRepository.findByKeyValueAndIsUsedFalse(accessKey).orElseThrow(() -> new RuntimeException("Invalid access key"));
        key.setUsed(true);
        accessKeyRepository.save(key);

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
        user.setEmailConfirmed(false);
        user.setAccessKey(accessKey);// Ustawienie jako niepotwierdzone

        // Zapis użytkownika w bazie danych
        userRepository.save(user);

        // Generowanie tokenu weryfikacyjnego
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(15); // Token ważny przez 24 godziny
        tokenRepository.save(token);

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

    public boolean initiatePasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Generowanie tokenu do resetu hasła
            ResetToken resetToken = new ResetToken();
            resetToken.setUser(user);
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpiryDate(60); // Token ważny przez 60 minut
            resetTokenRepository.save(resetToken);

            // Wysyłanie e-maila z linkiem do resetowania hasła
            sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
            return true;
        }
        return false;
    }

    private void sendPasswordResetEmail(String email, String token) {
        String subject = "Resetowanie hasła";
        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        String message = "Kliknij w poniższy link, aby zresetować swoje hasło:\n" + resetUrl;

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        mailSender.send(emailMessage);
    }

    public boolean validateResetToken(String token) {
        Optional<ResetToken> optionalToken = resetTokenRepository.findByToken(token);
        if (optionalToken.isPresent() && !optionalToken.get().isExpired()) {
            return true;
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<ResetToken> optionalToken = resetTokenRepository.findByToken(token);

        if (optionalToken.isPresent() && !optionalToken.get().isExpired()) {
            ResetToken resetToken = optionalToken.get();
            User user = resetToken.getUser();

            // Szyfrowanie nowego hasła
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);

            // Usunięcie tokenu po zresetowaniu hasła
            resetTokenRepository.delete(resetToken);

            return true;
        }
        return false;
    }
}