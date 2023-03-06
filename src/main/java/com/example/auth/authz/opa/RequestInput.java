package com.example.auth.authz.opa;

import com.example.auth.authz.authzv2.ResourceType;
import com.example.auth.authz.role.Permission;

import java.util.List;

public record RequestInput(String user,
                           List<String> teams,
                           Permission permissionsRequired,
                           String profileId,
                           ResourceType resourceType,
                           String recordId
) {
}
