package com.project.demo.model;

import com.project.demo.domain.VerificationType;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable  // ✅ Tells JPA this class is an embeddable value object
public class TwoFactorAuth {

    private boolean isEnabled = false;

    // EMAIL or MOBILE — reuse your enum from VerificationType
    private VerificationType sendTo;
}
