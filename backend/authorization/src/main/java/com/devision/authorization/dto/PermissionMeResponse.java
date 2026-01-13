package com.devision.authorization.dto;

import java.util.List;

public record PermissionMeResponse(
  String userId,
  String role,
  List<String> permissions
) {}
