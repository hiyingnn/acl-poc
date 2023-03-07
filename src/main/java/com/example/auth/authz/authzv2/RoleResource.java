package com.example.auth.authz.authzv2;

import com.example.auth.authz.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResource extends Resource {
  Role role;
}
