package com.example.auth.authz.authzv2;

import com.example.auth.authz.authzv1.facet.ProfileAcl;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FacetRecordAclRepository extends MongoRepository<FacetRecordAcl, String> {
    List<FacetRecordAcl> getFacetRecordAclByProfileId(Long profileId);

}
