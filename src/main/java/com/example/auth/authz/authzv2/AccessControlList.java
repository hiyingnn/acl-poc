package com.example.auth.authz.authzv2;

import com.example.auth.authz.role.Effect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.MongoId;

@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AccessControlList {
  @MongoId
  String id;
  enum Type {
    ROLE,
    RECORD
  };
  Type type;
  UserAndGroup userAndGroup;
  Long profileId;
  boolean isPermissionsDerived;
  Effect effect;
}
