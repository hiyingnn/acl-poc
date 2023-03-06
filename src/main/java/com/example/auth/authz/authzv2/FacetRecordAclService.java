package com.example.auth.authz.authzv2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacetRecordAclService {
  private final FacetRecordAclRepository facetRecordAclRepository;

  public RecordAcl addRecord(RecordAcl profileAcl) {
    return facetRecordAclRepository.save(profileAcl);
  }

  public List<AccessControlList> getAllRecords() {
    return facetRecordAclRepository
      .findAll();
  }

  public Optional<AccessControlList> getRecordByProfileId(String id) {
    return facetRecordAclRepository.findById(id);
  }

  public AccessControlList updateRecord(String id, AccessControlList recordAcl) {
    Optional<AccessControlList> existingFacetRecordAcl = facetRecordAclRepository.findById(id);

    if (existingFacetRecordAcl.isEmpty()) throw new IllegalArgumentException("Not found");

    AccessControlList accessControlList = null;
    if(recordAcl instanceof RecordAcl) {
       accessControlList = ((RecordAcl) recordAcl).toBuilder().id(existingFacetRecordAcl.get().getId()).build();

    } else if(recordAcl instanceof ProfileRoleAcl) {
       accessControlList = ((ProfileRoleAcl) recordAcl).toBuilder().id(existingFacetRecordAcl.get().getId()).build();

    }
    return facetRecordAclRepository.save(accessControlList);
  }


  public void deleteRecord(String id) {
    Optional<AccessControlList> existingFacetRecordAcl = facetRecordAclRepository.findById(id);

    if (existingFacetRecordAcl.isEmpty()) throw new IllegalArgumentException("Not found");

    facetRecordAclRepository.deleteById(existingFacetRecordAcl.get().getId());
  }

}
