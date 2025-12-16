package com.devision.authentication.dto;

import java.io.Serializable;

public record UserDto(String correlationId,
                      String email,
                      String fullName) implements Serializable {}
