package com.hospital.booking.dto;

import jakarta.validation.constraints.NotBlank;

public record BookingRequest(
        @NotBlank(message = "patientName is required")
        String patientName
) {
}
