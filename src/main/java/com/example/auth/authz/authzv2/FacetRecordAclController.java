package com.example.auth.authz.authzv2;

import com.example.auth.authz.CustomUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/facet-record-acl")
@RequiredArgsConstructor
public class FacetRecordAclController {
  private final FacetRecordAclService facetRecordAclService;

  @GetMapping
  public List<FacetRecordAcl> getAllRecords() {
    return facetRecordAclService.getAllRecords();
  }

  @GetMapping("{id}")
  public Optional<FacetRecordAcl> getOneRecord(@AuthenticationPrincipal CustomUser user, @PathVariable String id) {
    return facetRecordAclService.getRecordByProfileId(id);
  }

  @PostMapping
  public FacetRecordAcl addRecord(@AuthenticationPrincipal CustomUser user, @RequestBody @Valid FacetRecordAcl facetRecordAcl) {
    return facetRecordAclService.addRecord(facetRecordAcl);
  }

  @PutMapping("{id}")
  public FacetRecordAcl updateRecord(@RequestBody @Valid FacetRecordAcl facetRecordAcl, @PathVariable String id) {
    return facetRecordAclService.updateRecord(id, facetRecordAcl);
  }

  @DeleteMapping("{id}")
  public void deleteRecord(@PathVariable String id) {
    facetRecordAclService.deleteRecord(id);
  }

}
