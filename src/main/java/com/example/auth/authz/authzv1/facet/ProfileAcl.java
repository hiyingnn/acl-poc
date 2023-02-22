package com.example.auth.authz.authzv1.facet;

import com.example.auth.authz.role.TeamToRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Builder(toBuilder = true)
@TypeAlias("profileAcl")
@Document(collection = "profileAcl")
public class ProfileAcl {

  @MongoId
  String id;
  List<TeamToRole> teamToRoleList;

  @Indexed(unique = true)
  Long profileId;
}
