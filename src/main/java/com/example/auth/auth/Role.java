package com.example.auth.auth;

/**
 * This class contains the roles, and the permissions that belong to the role.
 * For the purpose of this PoC, we assume that this list is static.
 * <p>
 * If it were to be dynamic and customis-ability of roles are allowed, this can be persisted into a db
 */
public enum Role {
  RW_CAREER,
  R_CAREER
}
