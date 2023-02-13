package com.example.auth.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProfileAclRepository extends MongoRepository<ProfileAcl, String> {
  @Query("{\"teamToRoleList\": { $elemMatch:   { team: {$in:  ?1}, role: {$in: ?2 } }}, \"profileId\":?0 }")
  List<ProfileAcl> getProfileAclByProfileTeamsAndRole(Long profileId, String[] teams, Role[] roles);

}
