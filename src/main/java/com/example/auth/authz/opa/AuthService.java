package com.example.auth.authz.opa;

import com.example.auth.authz.CustomUser;
import com.example.auth.authz.authzv2.*;
import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.taxonomy.Facet;
import com.example.auth.authz.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final OpaClient opaClient;
  private final FacetRecordAclRepository facetRecordAclRepository;

  public boolean allow(Authentication authentication, Action action, Long profileId, Record record, String recordId) {
//    CustomUser user = (CustomUser) authentication.getPrincipal();

    CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List<RecordAcl> recordAcls = facetRecordAclRepository.getRecordAclByProfileIdAndRecordType(profileId, record);
    List<ProfileRoleAcl> profileRoleAcls = facetRecordAclRepository.getProfileRoleAclByProfileId(profileId);

    Facet facet = record.getFacetByRecord();

    Permission permissionRequired = facet.getPermissionByActionAndResource(action);
    DataInput dataInput = new DataInput(profileRoleAcls, recordAcls);


    RequestInput requestInput = new RequestInput(user.getUsername(), user.getTeams(),  permissionRequired, profileId, recordId, dataInput);

    return opaClient.isAllowed(new OpaRequest( requestInput));
  }

  public List<RecordAcl> getRecordOverwritePermissions(Action action, Long profileId, Record record) {
    CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<String> teams = user.getTeams();

    List<RecordAcl> recordAcls = facetRecordAclRepository.getRecordAclByProfileIdAndRecordType(profileId, record);

    Facet facet = record.getFacetByRecord();
    Permission permissionRequired = facet.getPermissionByActionAndResource(action);

    log.info(recordAcls.toString());
    List<RecordAcl> filteredRecordAcls = recordAcls.stream()
            .filter(acl ->
                    acl.getPermissions().contains(permissionRequired) &&

                    // This can be offloaded to policy engine?
                            (acl.getUserAndGroup().equals(new UserAndGroup(UserAndGroupType.USER, user.getUsername()))
                                    || teams.contains(acl.getUserAndGroup().getName()) // team needs stricter check
                            )

            )
            .toList();
    return filteredRecordAcls;
  }
}
