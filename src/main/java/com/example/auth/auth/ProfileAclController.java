package com.example.auth.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/acl")
@RequiredArgsConstructor
public class ProfileAclController {
  private final ProfileAclService profileAclService;

  @GetMapping
  public List<ProfileAcl> getAllRecords() {
    return profileAclService.getAllRecords();
  }

  @GetMapping("{profileId}")
  public Optional<ProfileAcl> getOneRecord(@AuthenticationPrincipal CustomUser user, @PathVariable Long profileId) {
    return profileAclService.getRecordByProfileId(profileId);
  }

  @PostMapping
  public ProfileAcl addRecord(@AuthenticationPrincipal CustomUser user, @RequestBody @Valid ProfileAcl profileAcl) {
    return profileAclService.addRecord(profileAcl);
  }

  @PutMapping("{profileId}")
  public ProfileAcl updateRecord(@RequestBody @Valid ProfileAcl profileAcl, @PathVariable Long profileId) {
    return profileAclService.updateRecord(profileId, profileAcl);
  }

  @DeleteMapping("{profileId}")
  public void deleteRecord(@PathVariable Long profileId) {
    profileAclService.deleteRecord(profileId);
  }

}
