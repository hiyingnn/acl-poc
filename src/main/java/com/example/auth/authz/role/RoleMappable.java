package com.example.auth.authz.role;

import java.util.List;

public interface RoleMappable {
  default List<Role> getRolesByPermission() {
    return List.of();
  }

}
