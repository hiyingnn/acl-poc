# Phase 1 of PoC
- Collection roles
- Document permissions overwrite

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
- Via team  to role list defined per profile

### Evaluation -  "policy"/"rule"
- ```java
  @Query("{\"teamToRoleList\": { $elemMatch:   { team: {$in:  ?1}, role: {$in: ?2 } }}, \"profileId\":?0 }")
   ```
- `allowableRoles` are defined as a roles that allow an action on a facet
  - For simplicity, these are defined in the enums
- **Basic idea: If a TeamRole tuple (user.teams, allowableRoles) exists in `teamToRoleList` - authorized**
- :bad: This policy is quite complex - will need to ensure if we offload to a policy engine, it would support such a request (but we know for sure that OPA would)

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
     - :to-note: Expression Handler `DefaultHttpSecurityExpressionHandler`  is unaware of Application Context, will need to set it

## v1 record overwrite permissions
### Modelling
```json
{
  "_id": "63eb433140bf2b65a481e957",
  "otherFields": "...",
  "recordAcls": [
    {
      "user": "alice",
      "effect": "GRANT",
      "permission": "R_CAREER"
    },
    {
      "user": "bob",
      "effect": "REVOKE",
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
  - Since fetch of record overwritten permissions is already fetched: Why not evaluate and short-circuit immediately if not authorized?
    
    
## Some thoughts...
1. Is there a way we can store both record and facet permissions in the same collection?
- Greater alignment with role, permissions?
```json
[
  {
    "identifier": "PROFILE_ADMIN",
    "user_and_group": {
      "type": "team",
      "name": "APPLE"
    },
    "resource": {
      "type": "facet",
      "resourceId": "careerHistory"
    },
    "profile": 1,
    "resourceId": null,
    "effect": "ALLOWED"
  },
  {
    "identifier": "",
    "user_and_group": {
      "type": "user",
      "name": "alice"
    },
    "resource": {
      "type": "record",
      "resourceId": "careerHistory/1234"
    },
    "profile": 1,
    "resourceId": "1234",
    "effect": "DENIED"
  }
]
```
2. Having better modelling will ensure that if we offload it to a policy engine, we can still query efficiently

### Research on frameworks for offloading/auth service
- Cerbos
- Zanzibar (open-source: permify, etc)

### Useful resources
- Slack RBAC -> granular permissions article
- TOADD
