package com.project.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.demo.domain.USER_ROLE;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    private String dob;
    private String nationality;
    private String address;
    private String city;
    private String postcode;
    private String country;

    // ✅ Keep this for true 2FA integration
    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    public boolean isAdmin() {
        return this.role == USER_ROLE.ROLE_ADMIN;
    }

    public boolean isCustomer() {
        return this.role == USER_ROLE.ROLE_CUSTOMER;
    }

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean twoFactorEnabled = false;

}
