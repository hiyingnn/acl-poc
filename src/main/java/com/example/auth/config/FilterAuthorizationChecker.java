package com.example.auth.config;

import com.example.auth.auth.*;
import com.example.auth.auth.taxonomy.Facet;
import com.example.auth.auth.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterAuthorizationChecker {
  private final ProfileAclRepository profileAclRepository;

  public boolean hasPermissionToActOnResource(Authentication authentication, Action action, Long profileId, Record resource) {
    CustomUser customUser = (CustomUser) authentication.getPrincipal();
    log.info("In filter auth");
    Facet facet = resource.getFacetByRecord();
    Permission permission = facet.getPermissionByActionAndResource(action);
    List<Role> permissibleRoles = permission.getRolesByPermission();

    List<String> teams = customUser.getTeams();

    String[] teamsArr = teams.toArray(new String[teams.size()]);
    Role[] rolesArr = permissibleRoles.toArray(new Role[permissibleRoles.size()]);
    log.info("profileId {}, teams {}, roles {}", profileId, teams, permissibleRoles);
    List<ProfileAcl> profileAcls = profileAclRepository.getProfileAclByProfileTeamsAndRole(profileId, teamsArr, rolesArr);
    log.info("{}", profileAcls);
    boolean hasPermission = profileAcls.size() > 0;

    return hasPermission;

  }
}
