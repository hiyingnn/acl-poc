package com.example.auth.authz.authzv2;

import com.example.auth.authz.role.Role;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class RoleResource extends Resource {
  Role role;
}
