package com.eventify.dto;

import com.eventify.entity.Registration;
import jakarta.validation.constraints.NotNull;

public class RegistrationStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private Registration.RegistrationStatus status;

    // Getters and Setters
    public Registration.RegistrationStatus getStatus() { return status; }
    public void setStatus(Registration.RegistrationStatus status) { this.status = status; }
}
