package com.devdivision.internal.entity;

public enum AdminRole {
    SUPER_ADMIN,
    ADMIN;

    public static AdminRole fromString(String value) {
        if (value == null) return null;
        return switch (value.trim().toUpperCase()) {
            case "ADMIN" -> ADMIN;
            case "SUPER_ADMIN", "SUPERADMIN" -> SUPER_ADMIN;
            default -> throw new IllegalArgumentException("Unknown role: " + value);
        };
    }
}
