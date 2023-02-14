package com.example.auth.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileAclService {
  private final ProfileAclRepository profileAclRepository;

  public ProfileAcl addRecord(ProfileAcl profileAcl) {
    return profileAclRepository.save(profileAcl);
  }

  public List<ProfileAcl> getAllRecords() {
    return profileAclRepository
      .findAll();
  }

  public Optional<ProfileAcl> getRecordByProfileId(Long profileId) {
    return profileAclRepository.getProfileAclByProfileId(profileId);
  }

  public ProfileAcl updateRecord(Long profileId, ProfileAcl profileAcl) {
    Optional<ProfileAcl> existingProfileAcl = profileAclRepository.getProfileAclByProfileId(profileId);

    if (existingProfileAcl.isEmpty()) throw new IllegalArgumentException("Not found");
    ProfileAcl profileAclToUpdate = profileAcl.toBuilder().id(existingProfileAcl.get().getId()).build();
    return profileAclRepository.save(profileAclToUpdate);
  }


  public void deleteRecord(Long profileId) {
    Optional<ProfileAcl> existingProfileAcl = profileAclRepository.getProfileAclByProfileId(profileId);

    if (existingProfileAcl.isEmpty()) throw new IllegalArgumentException("Not found");

    profileAclRepository.deleteById(existingProfileAcl.get().getId());
  }

}
