package com.example.auth.authz.authzv2;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Resource {
  ResourceType type;
}
