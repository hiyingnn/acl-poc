package com.example.auth.auth;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@TypeAlias("profileOwnership")
@Document(collection = "profileOwnership")
public class ProfileOwnership {
  Long profileId;
  List<TeamToRole> teamToRoleList;
}
