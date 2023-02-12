package com.example.auth.auth;

import com.example.auth.auth.taxonomy.Facet;
import com.example.auth.auth.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
  private final ProfileOwnershipRepository profileOwnershipRepository;

  public boolean hasPermissionToActOnResource(CustomUser customUser, Action action, Long profileId, Record resource) {
    Facet facet = resource.getFacetByRecord();
    Permission permission = facet.getPermissionByActionAndResource(action);
    List<Role> permissibleRoles = permission.getRolesByPermission();

    List<String> teams = customUser.getTeams();

    String[] teamsArr = teams.toArray(new String[teams.size()]);
    Role[] rolesArr = permissibleRoles.toArray(new Role[permissibleRoles.size()]);
    log.info("profileId {}, teams {}, roles {}", profileId, teams, permissibleRoles);
    List<ProfileOwnership> profileOwnerships = profileOwnershipRepository.getProfileOwnershipByProfileTeamsAndRole(profileId, teamsArr, rolesArr);
    log.info("{}", profileOwnerships);
    boolean hasPermission = profileOwnerships.size() > 0;

    return hasPermission;

  }
}
