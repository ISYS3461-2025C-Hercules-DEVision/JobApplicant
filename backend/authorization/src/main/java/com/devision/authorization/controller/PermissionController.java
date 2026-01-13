package com.devision.authorization.controller;

import com.devision.authorization.dto.PermissionMeResponse;
import com.devision.authorization.service.PermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

  private final PermissionService permissionService;

  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @GetMapping("/me")
  public PermissionMeResponse me(Authentication auth) {
    String userId = auth.getName();

    // Spring stores authorities like ROLE_SUPER_ADMIN etc.
    String role = auth.getAuthorities().stream()
      .findFirst()
      .map(a -> a.getAuthority().replace("ROLE_", ""))
      .orElse("");

    List<String> perms = permissionService.permissionsForRole(role);

    return new PermissionMeResponse(userId, role, perms);
  }
}
