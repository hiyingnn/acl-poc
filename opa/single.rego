# first- filter is OPA:
# Facet + Record filter on fetch single resource

package single

import future.keywords.contains
import future.keywords.if
import future.keywords.in

# By default, deny requests.
default allow := false

# # role-permissions assignments: defined here first (may be part of data)
# permissions_to_action := {
# 	"R_CAREER": {"action": "read",  "object": "careerHistory"},
#     "W_CAREER": {"action": "write",  "object": "careerHistory"}
# }

role_to_permissions := {"CAREER_ADMIN": ["R_CAREER", "W_CAREER"], "CAREER_VIEWER": ["R_CAREER"]}

# Expand profile role to permissions
profile_role_permissions = [user_permissions_effect |
    user_group := input.data.profileRoles[i].userAndGroup
    role := input.data.profileRoles[i].resource.role
    permissions := role_to_permissions[role]
    user_permissions := object.union({"user_group": user_group}, {"permissions": permissions})
    user_permissions_effect := object.union(user_permissions, {"effect": "ALLOW"})

	is_user_group(user_group)
	input.data.profileRoles[i].resource.type == "ROLE"
    is_permission_granted(permissions)

]

# Record permissions
record_permissions = [user_permissions_effect |
    user_group := input.data.recordPermissions[i].userAndGroup
    permissions := input.data.recordPermissions[i].permissions
    effect := input.data.recordPermissions[i].effect
    record_id := input.data.recordPermissions[i].resource.recordId

    user_permissions := object.union({"user_group": user_group}, {"permissions": permissions})
    user_permissions_effect := object.union(user_permissions, {"effect": effect})

	is_user_group(user_group)
    is_record(record_id)
	input.data.recordPermissions[i].resource.type == "RECORD"
    is_permission_granted(permissions)
]

# Facet permission: 1, Record permission: 0 (REVOKE) -> 0
# Facet permission: 0, Record permission: 1 (GRANTED) -> 1
# Facet permission: 1, Record permission: - (MISSING) -> 1
# Facet permission: 0, Record permission: - (MISSING) -> 0
allow := true if {
 	count(record_permissions) > 0
    effect_allowed(record_permissions)
    not effect_revoked(record_permissions)
}

allow := true if  {
	count(record_permissions) = 0
    effect_allowed(profile_role_permissions)
    not effect_revoked(profile_role_permissions)

}

effect_allowed(permissions) if {
	some permission in permissions
    permission.effect == "ALLOW"
}

effect_revoked(permissions) if {
	some permission in permissions
    permission.effect == "DENY"
}

is_user_group(user_and_group) if {
    user_and_group.type == "TEAM"
    user_and_group.name in input.teams
}

is_user_group(user_and_group) if {
    user_and_group.type == "USER"
    user_and_group.name == input.user
}

is_record(record_id) if {
    record_id == input.recordId
}

is_permission_granted(permissions) if {
	input.permissionRequired in permissions
}
