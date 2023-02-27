# Phase 1 of PoC

- Collection roles
- Document permissions overwrite

## Overview

- v1 (Phase 1): Spring security via Method Security or Authz filter
  - Facet permissions: profile-team-role mapping `ProfileAcl` collection
  - Record permissions: embedded within document
  - **Within `authzv1` package**
- v2 (Phase 2): Rethinking a model where we can easily offload to policy engine
  - Facet/Record permissions in the same collection
  - **Within `authzv2` package**

## Users

- Fixed users defined in: `CustomUserDetailsService`
- Authorization type: Basic Auth, with password `password`

| user  | teams             |
|-------|:------------------|
| alice | APPLE, BANANA     |
| bob   | STARFRUIT, ORANGE |

## v1 Collection permissions

### Modelling

``` json
{
  "_id":  "63eb3dbe40bf2b65a481e955",
  "teamToRoleList": [
    {
      "team": "STARFRUIT",
      "role": "R_CAREER"
    }
  ],
  "profileId": 1
}
```

- Via team to role list defined per profile

### Evaluation -  "policy"/"rule"

- ```java
  @Query("{\"teamToRoleList\": { $elemMatch:   { team: {$in:  ?1}, role: {$in: ?2 } }}, \"profileId\":?0 }")
   ```
- `allowableRoles` are defined as a roles that allow an action on a facet
  - For simplicity, these are defined in the enums
- **Basic idea: If a TeamRole tuple (user.teams, allowableRoles) exists in `teamToRoleList` - authorized**
- :bad: This policy is quite complex - will need to ensure if we offload to a policy engine, it would support such a
  request (but we know for sure that OPA would)

### Evaluation - point

- Can be done at various points:
  1. `@PreAuthorise` Method security

  - :good: Per method pass in parameters
  - :bad: Repeated code, Authz concerns in controller

  2. Custom `AuthorizationManager` (which supersedes `AccessDecisionManager` and `AccessDecisionVoter`)

  - :good: Can invoke any external policy agent here
  - :bad: Will need to derive path variables/ other contextual information by parsing request URI

  3. `WebExpressionAuthorizationManager`:

  - :good: able to pass in context like path variables and define based on endpoint
  - :to-note: Expression Handler `DefaultHttpSecurityExpressionHandler`  is unaware of Application Context, will need
    to set it

## v1 record overwrite permissions

### Modelling

```json
{
  "_id": "63eb433140bf2b65a481e957",
  "otherFields": "...",
  "recordAcls": [
    {
      "user": "alice",
      "effect": "ALLOW",
      "permission": "R_CAREER"
    },
    {
      "user": "bob",
      "effect": "DENY",
      "permission": "R_CAREER"
    }
  ]
}
```

- Stored together in document itself to prevent "desyncs"

### Evaluation -  "policy"/"rule"

- ```
     Optional<Effect> recordOverwriteResult = recordOverwrite.get(0).getRecordAcls().stream().filter(
                record -> record.getPermission().equals(permission)
                        && record.getUser().equals(customUser.getUsername())
        ).map(RecordOverwriteAcl::getEffect).findFirst();
  ```
  - permission is defined by the (action, resource)
  - **Basic idea: Fetch a user permission effect based on (user.userName, permission):**
    - if effect is `REVOKE`: not authorized
    - `GRANTED`: authorized

### Evaluation - point

- `MongoTemplate` is used and the collection to be queried is derived from the record
- **Previous thoughts on using a flag to continue and not short-circuit :**
  - Since fetch of record overwritten permissions is already fetched: Why not evaluate and short-circuit immediately if
    not authorized?

## v2 facet+record permissions

1. Is there a way we can store both record and facet permissions in the same collection?

- Greater alignment with role, permissions?
- Initial thoughts on modelling:

``` json
[
  {
    /** team based facet permissions**/
    "identifier": "CAREER_ADMIN",
    "userAndGroup": {
      "type": "TEAM",
      "name": "APPLE"
    },
    "resource": {
      "type": "ROLE",
      "resourceId": "CAREER_ADMIN"
    },
    "profile": 1,
    "isPermissionsDerived": "true", // permission is derived by Role, may expand to ["R_CAREER", "W_CAREER"]
    "effect": "ALLOW"
  },
   {
    /** user based record overwrite permissions**/
    "identifier": "PROFILE",
    "userAndGroup": {
      "type": "USER",
      "name": "alice"
    },
    "resource": {
      "type": "RECORD",
      "resourceId": "careerHistory/1234"
    },
    "profile": 1,
    "isPermissionsDerived": "false",
    "permissions": ["R_CAREER"],
    "effect": "ALLOW"
  },
]
```

- Having better modelling will ensure that if we offload it to a policy engine, we can still query efficiently

### Research on frameworks for offloading/auth service

- Cerbos
- Zanzibar (open-source: permify, etc)

### Useful resources

- Slack RBAC -> granular permissions article
- TOADD

## OPA

```opa
# Role-based Access Control (RBAC)
# --------------------------------
#
# This example defines an RBAC model for a Pet Store API. The Pet Store API allows
# users to look at pets, adopt them, update their stats, and so on. The policy
# controls which users can perform actions on which resources. The policy implements
# a classic Role-based Access Control model where users are assigned to roles and
# roles are granted the ability to perform some action(s) on some type of resource.
#
# This example shows how to:
#
#	* Define an RBAC model in Rego that interprets role mappings represented in JSON.
#	* Iterate/search across JSON data structures (e.g., role mappings)
#
# For more information see:
#
#	* Rego comparison to other systems: https://www.openpolicyagent.org/docs/latest/comparison-to-other-systems/
#	* Rego Iteration: https://www.openpolicyagent.org/docs/latest/#iteration

package app.rbac

import future.keywords.contains
import future.keywords.if
import future.keywords.in

# By default, deny requests.
default allow := false

# # role-permissions assignments: defined here first (may be part of data)
# permissions := {
# 	"R_CAREER": {"action": "read",  "object": "careerHistory"},
#     "W_CAREER": {"action": "write",  "object": "careerHistory"}
# }

role_permissions := {"CAREER_ADMIN": ["R_CAREER", "W_CAREER"]}

# Expand role
profile_role_permissions = [user_permissions_effect |
user_group := data.profileRoles[i].userAndGroup
role := data.profileRoles[i].resource.resourceId
permissions := role_permissions[role]
user_permissions := object.union({"user_group": user_group}, {"permissions": permissions})
user_permissions_effect := object.union(user_permissions, {"effect": "ALLOW"})

	is_user_group(user_group)
	data.profileRoles[i].resource.type == "ROLE"
]

# Record permissions
record_permissions = [user_permissions_effect |
user_group := data.recordPermissions[i].userAndGroup
permissions := data.recordPermissions[i].permissions
user_permissions := object.union({"user_group": user_group}, {"permissions": permissions})
user_permissions_effect := object.union(user_permissions, {"effect": "ALLOW"})

	is_user_group(user_group)
	data.recordPermissions[i].resource.type == "RECORD"
]

is_user_group(user_and_group) if {
user_and_group.type == "TEAM"
user_and_group.name in input.teams
}

is_user_group(user_and_group) if {
user_and_group.type == "USER"
user_and_group.name == input.user
}
```

```aidl
{
    "user": "alice",
    "teams": [
        "APPLE",
        "STARFRUIT"
    ],
    "action": "read",
    "profileId": 1,
    "objectType": "careerHistory/1234",
    "resourceId": "1234"
}
```

```aidl
{
    "profileRoles": [
        {
            "identifier": "PROFILE_ROLE",
            "userAndGroup": {
                "type": "TEAM",
                "name": "APPLE"
            },
            "resource": {
                "type": "ROLE",
                "resourceId": "CAREER_ADMIN"
            },
            "profile": 1,
            "isPermissionsDerived": "true",
            "effect": "ALLOW"
        },
        {
            "identifier": "PROFILE_ROLE",
            "userAndGroup": {
                "type": "USER",
                "name": "alice"
            },
            "resource": {
                "type": "ROLE",
                "resourceId": "CAREER_ADMIN"
            },
            "profile": 1,
            "isPermissionsDerived": "true",
            "effect": "ALLOW"
        }
    ],
    "recordPermissions": [
        {
            "identifier": "RECORD_PERMISSIONS",
            "userAndGroup": {
                "type": "USER",
                "name": "alice"
            },
            "resource": {
                "type": "RECORD",
                "resourceId": "careerHistory/1234"
            },
            "profile": 1,
            "isPermissionsDerived": "false",
            "permissions": [
                "R_CAREER"
            ],
            "effect": "ALLOW"
        }
    ]
}
```
