package com.example.auth.authz.role;

import java.util.List;

public enum Permission implements RoleMappable {
  W_CAREER {
    @Override
    public List<Role> getRolesByPermission() {
      return List.of(Role.RW_CAREER);
    }


  },
  R_CAREER {
    @Override
    public List<Role> getRolesByPermission() {
      return List.of(Role.RW_CAREER, Role.R_CAREER);
    }
  }
}
