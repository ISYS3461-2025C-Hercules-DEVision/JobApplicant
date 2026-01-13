package com.devision.authorization.controller;

import com.devision.authorization.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/policies")
public class PolicyController {

  private final PermissionService permissionService;

  public PolicyController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @GetMapping
  public Map<String, Object> listPolicies() {
    return Map.of(
      "mode", "DEFAULT_IN_CODE",
      "roles", Map.of(
        "SUPER_ADMIN", permissionService.permissionsForRole("SUPER_ADMIN"),
        "APPLICANT", permissionService.permissionsForRole("APPLICANT"),
        "COMPANY", permissionService.permissionsForRole("COMPANY")
      )
    );
  }
}
