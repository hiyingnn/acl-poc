package com.example.auth.authz.authzv2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacetRecordAclService {
    private final FacetRecordAclRepository facetRecordAclRepository;

    public FacetRecordAcl addRecord(FacetRecordAcl profileAcl) {
        return facetRecordAclRepository.save(profileAcl);
    }

    public List<FacetRecordAcl> getAllRecords() {
        return facetRecordAclRepository
                .findAll();
    }

    public Optional<FacetRecordAcl> getRecordByProfileId(String id) {
        return facetRecordAclRepository.findById(id);
    }

    public FacetRecordAcl updateRecord(String id, FacetRecordAcl facetRecordAcl) {
        Optional<FacetRecordAcl> existingFacetRecordAcl = facetRecordAclRepository.findById(id);

        if (existingFacetRecordAcl.isEmpty()) throw new IllegalArgumentException("Not found");
        FacetRecordAcl facetRecordAclToUpdate = facetRecordAcl.toBuilder().id(existingFacetRecordAcl.get().getId()).build();
        return facetRecordAclRepository.save(facetRecordAclToUpdate);
    }


    public void deleteRecord(String id) {
        Optional<FacetRecordAcl> existingFacetRecordAcl = facetRecordAclRepository.findById(id);

        if (existingFacetRecordAcl.isEmpty()) throw new IllegalArgumentException("Not found");

        facetRecordAclRepository.deleteById(existingFacetRecordAcl.get().getId());
    }

}
