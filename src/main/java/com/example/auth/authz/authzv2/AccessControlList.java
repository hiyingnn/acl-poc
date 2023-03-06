package com.example.auth.authz.authzv2;

import com.example.auth.authz.role.Effect;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.MongoId;

@SuperBuilder(toBuilder = true)
@Data
public abstract class AccessControlList {
  @MongoId
  String id;
  String identifier;
  UserAndGroup userAndGroup;
  Long profileId;
  boolean isPermissionsDerived;
  Effect effect;
}
