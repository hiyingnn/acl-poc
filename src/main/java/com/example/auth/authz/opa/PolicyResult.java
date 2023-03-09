package com.example.auth.authz.opa;

import java.util.List;

public record PolicyResult(boolean allow,
                           List<PermissionResult> profile_role_permissions,
                           List<PermissionResult> record_permissions
                          ) {
}
