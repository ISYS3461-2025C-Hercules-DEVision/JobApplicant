package com.devision.application.dto;

import com.devision.application.enums.ApplicationStatus;

public record UpdateStatusRequest(ApplicationStatus status) {
}
