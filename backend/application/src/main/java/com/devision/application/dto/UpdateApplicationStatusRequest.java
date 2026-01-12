package com.devision.application.dto;


import jakarta.validation.constraints.NotBlank;

public record UpdateApplicationStatusRequest(
        @NotBlank String newStatus,
        String feedback,
        @NotBlank String applicationId
) {}

