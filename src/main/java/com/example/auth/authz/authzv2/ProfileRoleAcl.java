package com.example.auth.authz.authzv2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder(toBuilder = true)
@TypeAlias("facetRecordAcl")
@Document(collection = "facetRecordAcl")
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRoleAcl extends AccessControlList {
  RoleResource resource;
}
