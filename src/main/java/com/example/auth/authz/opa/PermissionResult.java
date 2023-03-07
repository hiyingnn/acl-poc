package com.example.auth.authz.opa;

import com.example.auth.authz.authzv2.UserAndGroup;
import com.example.auth.authz.role.Effect;
import com.example.auth.authz.role.Permission;

import java.util.List;

public record PermissionResult(
        UserAndGroup user_group,
        List<Permission> permissions,
        Effect effect
) {
}
