package com.example.auth.authz.role;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class contains the team and the role it is assigned to.
 */
@Data
@AllArgsConstructor
public class TeamToRole {
  String team;
  Role role;
}
