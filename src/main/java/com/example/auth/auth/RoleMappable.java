package com.example.auth.auth;

import java.util.List;

public interface RoleMappable {
  default List<Role> getRolesByPermission() {
    return List.of();
  }

}
