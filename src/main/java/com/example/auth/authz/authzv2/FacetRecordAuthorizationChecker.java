package com.example.auth.authz.authzv2;

import com.example.auth.authz.CustomUser;
import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Effect;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.role.Role;
import com.example.auth.authz.taxonomy.Facet;
import com.example.auth.authz.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FacetRecordAuthorizationChecker {
  private final FacetRecordAclRepository facetRecordAclRepository;

  public boolean hasPermissionToActOnResource(Authentication authentication, Action action, Long profileId, Record resource, String resourceId) {
    log.info("In filter auth facet-record");
    CustomUser user = (CustomUser) authentication.getPrincipal();
    Set<String> teams = new HashSet<>(user.getTeams());

    Facet facet = resource.getFacetByRecord();
    Permission permission = facet.getPermissionByActionAndResource(action);

    /*
     * Facet permission: 1, Record permission: 0 (REVOKE) -> 0
     * Facet permission: 0, Record permission: 1 (GRANTED) -> 1
     * Facet permission: 1, Record permission: - (MISSING) -> 1
     * Facet permission: 0, Record permission: - (MISSING) -> 0
     *
     */

    List<RecordAcl> recordPermissions = facetRecordAclRepository.getRecordAclByProfileId(profileId);

    List<ProfileRoleAcl> profileRoles = facetRecordAclRepository.getProfileRoleAclByProfileId(profileId);

    // Check for record permissions first
    String recordResourceId = String.format("%s/%s", resource.getCollectionName(), resourceId);
    List<RecordAcl> recordAcls = recordPermissions.stream()
      .filter(acl ->

        // This can be offloaded to policy engine?
        acl.getResource().getType().equals(ResourceType.RECORD)
          && acl.getResource().getRecordId().equals(recordResourceId) &&
          acl.getPermissions().contains(permission) &&
          (acl.getUserAndGroup().equals(new UserAndGroup(UserAndGroupType.USER, user.getUsername()))
            || teams.contains(acl.getUserAndGroup().getName()) // team needs stricter check
          )

      )
      .toList();

    // Evaluate recordAcl
    Optional<RecordAcl> firstRecordAcl = recordAcls.stream().findFirst();
    if (firstRecordAcl.isPresent()) {
      Effect effect = firstRecordAcl.get().getEffect();
      return effect.equals(Effect.ALLOW);
    }

    // Evaluate by role
    List<String> allowableRoles = permission.getRolesByPermission().stream().map(Role::toString).toList();

    // Get facet acl
    List<ProfileRoleAcl> profileRoleAcls = profileRoles.stream()
      .filter(acl ->
        acl.getResource().getType().equals(ResourceType.ROLE) &&
          allowableRoles.contains(acl.getResource().getRole().name()) &&
          teams.contains(acl.getUserAndGroup().getName()) // team needs stricter check
      )
      .toList();

    // Evaluate facetAcl
    Optional<ProfileRoleAcl> firstFacetAcl = profileRoleAcls.stream().findFirst();
    if (firstFacetAcl.isPresent()) {
      Effect effect = firstFacetAcl.get().getEffect();
      return effect.equals(Effect.ALLOW);
    }

    return false;
  }
}
