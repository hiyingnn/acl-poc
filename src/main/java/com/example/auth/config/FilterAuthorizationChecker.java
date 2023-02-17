package com.example.auth.config;

import com.example.auth.auth.*;
import com.example.auth.auth.taxonomy.Facet;
import com.example.auth.auth.taxonomy.Record;
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
            return recordOverwriteResult.equals(RecordOverwriteResult.GRANTED);
        }
    }
}
