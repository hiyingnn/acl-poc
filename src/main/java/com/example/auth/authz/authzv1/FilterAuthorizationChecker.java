package com.example.auth.authz.authzv1;

import com.example.auth.authz.CustomUser;
import com.example.auth.authz.authzv1.facet.FacetAuthorizationChecker;
import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.role.RecordOverwriteResult;
import com.example.auth.authz.authzv1.recordoverwrite.RecordOverwriteAuthorizationChecker;
import com.example.auth.authz.taxonomy.Facet;
import com.example.auth.authz.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterAuthorizationChecker {
    private final FacetAuthorizationChecker facetAuthorizationChecker;
    private final RecordOverwriteAuthorizationChecker recordOverwriteAuthorizationChecker;

    public boolean hasPermissionToActOnResource(Authentication authentication, Action action, Long profileId, Record resource, String resourceId) {
        log.info("In filter auth");
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        Facet facet = resource.getFacetByRecord();
        Permission permission = facet.getPermissionByActionAndResource(action);

        /*
         * Facet permission: 1, Record permission: 0 (REVOKE) -> 0
         * Facet permission: 0, Record permission: 1 (GRANTED) -> 1
         * Facet permission: 1, Record permission: - (MISSING) -> 1
         * Facet permission: 0, Record permission: - (MISSING) -> 0
         */
        boolean isFacetGranted = facetAuthorizationChecker.hasPermissionToActOnResource(customUser, permission, profileId);
        RecordOverwriteResult recordOverwriteResult = recordOverwriteAuthorizationChecker.hasPermissionToActOnResource(customUser, permission, profileId, resource, resourceId);

        if (recordOverwriteResult.equals(RecordOverwriteResult.NOT_OVERWRITTEN)) {
            return isFacetGranted;
        } else {
            return recordOverwriteResult.equals(RecordOverwriteResult.ALLOWED);
        }
    }
}
