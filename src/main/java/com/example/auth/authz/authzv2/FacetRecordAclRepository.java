package com.example.auth.authz.authzv2;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FacetRecordAclRepository extends MongoRepository<AccessControlList, String> {
 List<RecordAcl> getRecordAclByProfileId(Long profileId);

 List<ProfileRoleAcl> getProfileRoleAclByProfileId(Long profileId);

}
