package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.repository.ResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private ResetTokenRepository resetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@example.com";
        String password = "password";
        String role = "USER";
        String address = "123 Main St";
        String phoneNumber = "1234567890";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // doNothing().when(mailSender).send((MimeMessage) any()); // Commented out to avoid sending email

        User registeredUser = userService.registerUser(name, surname, email, password, role, address, phoneNumber);

        assertNotNull(registeredUser);
        assertEquals(name, registeredUser.getName());
        assertEquals(surname, registeredUser.getSurname());
        assertEquals(email, registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(role, registeredUser.getRole());
        assertEquals(address, registeredUser.getAddress());
        assertEquals(phoneNumber, registeredUser.getPhoneNumber());
        assertFalse(registeredUser.isEmailConfirmed());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
        // verify(mailSender, times(1)).send((MimeMessage) any()); // Commented out to avoid sending email
    }
}