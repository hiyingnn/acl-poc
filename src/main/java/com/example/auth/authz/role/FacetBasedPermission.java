package com.example.auth.authz.role;


import java.util.List;

/**
 * Permission is defined as an action on a particular resource (i.e., facet)
 * </br>
 * For the purpose of this PoC, we assume that this list is static.
 * <p>
 * Can contain a map of the resource to the role
 */
public enum FacetBasedPermission implements RoleMappable {
  R_CAREER {
    @Override
    public List<Role> getRolesByPermission() {
      return List.of(Role.R_CAREER, Role.RW_CAREER);
    }
  },
  W_CAREER {
    @Override
    public List<Role> getRolesByPermission() {
      return List.of(Role.RW_CAREER);
    }
  }
}
