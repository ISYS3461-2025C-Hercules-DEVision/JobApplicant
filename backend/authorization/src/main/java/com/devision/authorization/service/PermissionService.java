package com.devision.authorization.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService {

  public List<String> permissionsForRole(String role) {
    String r = role == null ? "" : role.trim().toUpperCase();
    if ("COPANY".equals(r)) r = "COMPANY";

    List<String> perms = new ArrayList<>();

    switch (r) {
      case "SUPER_ADMIN" -> {
        perms.add("POLICY_MANAGE");
        perms.add("ADMIN_APPLICANT_READ_ALL");
        perms.add("ADMIN_APPLICATION_READ_ALL");
        perms.add("ADMIN_JOB_READ_ALL");
        perms.add("ADMIN_COMPANY_READ_ALL");
      }
      case "APPLICANT" -> {
        perms.add("APPLICANT_PROFILE_READ");
        perms.add("APPLICANT_PROFILE_UPDATE");
        perms.add("APPLICANT_APPLICATION_CREATE");
        perms.add("APPLICANT_APPLICATION_READ_OWN");
      }
      case "COMPANY" -> {
        perms.add("COMPANY_APPLICATION_READ");
        perms.add("COMPANY_APPLICATION_UPDATE_STATUS");
        perms.add("COMPANY_JOB_MANAGE");
      }
      default -> {
        // no perms
      }
    }

    return perms;
  }
}
