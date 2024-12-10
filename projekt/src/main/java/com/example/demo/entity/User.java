package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users1")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "surname", nullable = false, length = 20)
    private String surname;

    @Column(name = "email", nullable = false, length = 40)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", length = 40)
    private String role;

    @Column(name = "address", length = 100)
    private String address;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "account_creation_date")
    @Temporal(TemporalType.DATE)
    private Date accountCreationDate;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "is_email_confirmed")
    private boolean isEmailConfirmed = false;

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public boolean isEmailConfirmed() {
        return isEmailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        isEmailConfirmed = emailConfirmed;
    }
}