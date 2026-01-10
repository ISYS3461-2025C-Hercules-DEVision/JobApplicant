package com.example.application.application.dto;


import com.example.application.application.enums.ApplicationStatus;

public record UpdateStatusRequest(ApplicationStatus status) {
}
