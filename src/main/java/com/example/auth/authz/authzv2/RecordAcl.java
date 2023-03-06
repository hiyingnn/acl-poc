package com.example.auth.authz.authzv2;

import com.example.auth.authz.role.Permission;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@TypeAlias("facetRecordAcl")
@Document(collection = "facetRecordAcl")
public class RecordAcl extends AccessControlList {
  RecordResource resource;
  List<Permission> permissions;
}
