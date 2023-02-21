package com.example.auth.auth;

import com.example.auth.auth.*;
import com.example.auth.auth.taxonomy.Facet;
import com.example.auth.auth.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FacetAuthorizationChecker {
    private final ProfileAclRepository profileAclRepository;

    public boolean hasPermissionToActOnResource(CustomUser customUser, Permission permission, Long profileId) {
        log.info("In filter auth: facet");

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
