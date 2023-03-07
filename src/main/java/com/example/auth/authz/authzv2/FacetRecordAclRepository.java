package com.example.auth.authz.authzv2;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FacetRecordAclRepository extends MongoRepository<AccessControlList, String> {
 @Query("{ 'resource.type' : \"RECORD\",  'profileId': ?0 }")
 List<RecordAcl> getRecordAclByProfileId(Long profileId);

 @Query("{ 'resource.type' : \"ROLE\",  'profileId': ?0 }")
 List<ProfileRoleAcl> getProfileRoleAclByProfileId(Long profileId);

}
