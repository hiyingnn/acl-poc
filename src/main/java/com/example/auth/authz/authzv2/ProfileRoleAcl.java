package com.example.auth.authz.authzv2;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder(toBuilder = true)
@TypeAlias("facetRecordAcl")
@Document(collection = "facetRecordAcl")
public class ProfileRoleAcl extends AccessControlList {
  RoleResource resource;
}
