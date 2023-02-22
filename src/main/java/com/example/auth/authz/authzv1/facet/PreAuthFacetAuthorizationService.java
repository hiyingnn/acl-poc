package com.example.auth.authz.authzv1.facet;

import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.role.Role;
import com.example.auth.authz.CustomUser;
import com.example.auth.authz.taxonomy.Facet;
import com.example.auth.authz.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Annotate with @PreAuthorize in Controller/Service layer, and SpEL expression to invoke this method
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PreAuthFacetAuthorizationService {
  private final ProfileAclRepository profileAclRepository;

  public boolean hasPermissionToActOnResource(CustomUser customUser, Action action, Long profileId, Record resource) {
    Facet facet = resource.getFacetByRecord();
    Permission permission = facet.getPermissionByActionAndResource(action);
    List<Role> permissibleRoles = permission.getRolesByPermission();

    List<String> teams = customUser.getTeams();

    String[] teamsArr = teams.toArray(new String[teams.size()]);
    Role[] rolesArr = permissibleRoles.toArray(new Role[permissibleRoles.size()]);
    log.info("profileId {}, teams {}, roles {}", profileId, teams, permissibleRoles);
    List<ProfileAcl> profileAcls = profileAclRepository.getProfileAclByProfileTeamsAndRole(profileId, teamsArr, rolesArr);
    log.info("{}", profileAcls);

    return profileAcls.size() > 0;

  }
}
