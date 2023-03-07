package com.example.auth.authz.opa;

import com.example.auth.authz.CustomUser;
import com.example.auth.authz.authzv2.*;
import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.taxonomy.Facet;
import com.example.auth.authz.taxonomy.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpaService {

  private final OpaClient opaClient;
  private final FacetRecordAclRepository facetRecordAclRepository;

  public boolean allow(Authentication authentication, Action action, Long profileId, Record record, String recordId) {
    CustomUser user = (CustomUser) authentication.getPrincipal();


    List<RecordAcl> recordAcls = facetRecordAclRepository.getRecordAclByProfileId(profileId);
    List<ProfileRoleAcl> profileRoleAcls = facetRecordAclRepository.getProfileRoleAclByProfileId(profileId);

    Facet facet = record.getFacetByRecord();

    Permission permissionRequired = facet.getPermissionByActionAndResource(action);
    DataInput dataInput = new DataInput(profileRoleAcls, recordAcls);

    RequestInput requestInput = new RequestInput(user.getUsername(), user.getTeams(),  permissionRequired, profileId, recordId);

    return opaClient.isAllowed(new OpaRequest(dataInput, requestInput));

  }
}
