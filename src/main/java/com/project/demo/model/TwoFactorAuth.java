package com.project.demo.model;

import com.project.demo.domain.VerificationType;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class TwoFactorAuth {

    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private VerificationType sendTo; // EMAIL or MOBILE
}
