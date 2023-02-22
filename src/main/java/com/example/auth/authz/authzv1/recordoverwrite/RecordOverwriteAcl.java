package com.example.auth.authz.authzv1.recordoverwrite;

import com.example.auth.authz.role.Effect;
import com.example.auth.authz.role.Permission;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordOverwriteAcl {
    String user;
    Effect effect;
    Permission permission;
}
