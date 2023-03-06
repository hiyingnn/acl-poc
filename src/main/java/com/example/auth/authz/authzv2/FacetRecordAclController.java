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
  public List<AccessControlList> getAllRecords() {
    return facetRecordAclService.getAllRecords();
  }

  @GetMapping("{id}")
  public Optional<AccessControlList> getOneRecord(@AuthenticationPrincipal CustomUser user, @PathVariable String id) {
    return facetRecordAclService.getRecordByProfileId(id);
  }

  @PostMapping
  public AccessControlList addRecord(@AuthenticationPrincipal CustomUser user, @RequestBody @Valid RecordAcl recordAcl) {
    return facetRecordAclService.addRecord(recordAcl);
  }

  @PutMapping("{id}")
  public AccessControlList updateRecord(@RequestBody @Valid AccessControlList recordAcl, @PathVariable String id) {
    return facetRecordAclService.updateRecord(id, recordAcl);
  }

  @DeleteMapping("{id}")
  public void deleteRecord(@PathVariable String id) {
    facetRecordAclService.deleteRecord(id);
  }

}
