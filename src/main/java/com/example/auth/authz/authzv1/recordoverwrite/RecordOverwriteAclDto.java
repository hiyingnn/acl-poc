package com.example.auth.authz.authzv1.recordoverwrite;

import com.example.auth.authz.role.Permission;
import com.example.auth.authz.role.Effect;

public record RecordOverwriteAclDto (String user,
        Effect effect,
        Permission permission){
}
