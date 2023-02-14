package com.example.auth.auth;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder(toBuilder = true)
@TypeAlias("profileAcl")
@Document(collection = "profileAcl")
public class ProfileAcl {

  @Id
  String id;
  List<TeamToRole> teamToRoleList;

  @Indexed(unique = true)
  Long profileId;
}
