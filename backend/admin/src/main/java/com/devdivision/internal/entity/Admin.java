package com.devdivision.internal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("admins")
public class Admin {
    @Id
    private String adminId;
    private String adminEmail;
    private AdminRole adminRole;

    public Admin(String adminId, String adminEmail, AdminRole adminRole) {
        this.adminId = adminId;
        this.adminEmail = adminEmail;
        this.adminRole = adminRole;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public AdminRole getAdminRole() {
        return adminRole;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }
}
