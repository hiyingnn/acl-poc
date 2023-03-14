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

## Phase 2 Externalising with OPA
- Using docker to run OPA server 
- Docker image: `openpolicyagent/opa`

#### Initial set-up
- More information found in: https://www.openpolicyagent.org/docs/latest/deployments/#running-with-docker

1. Run OPA
```
-- run server
docker run -p 8181:8181 openpolicyagent/opa \
    run --server --log-level debug
    
-- check if OPA is available 
curl -i localhost:8181/
```

2. Add Policy via Policy API
``` 
-- Create new policy
curl -X PUT --data-binary \@single.rego localhost:8181/v1/policies/single

-- Check policy created
curl localhost:8181/v1/policies/single?pretty=true

-- Test policy evaluatin
curl -X POST localhost:8181/v1/data/single?pretty=true -d @v1-data-input.json -H 'Content-Type: application/json'
```

3. In app policy evaluation via Data API 
``` java
@Service
@Slf4j
public class OpaClient {
  private final RestTemplate restTemplate;

  public OpaClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
            .rootUri("http://localhost:8181")
            .build();
  }

  public boolean isAllowed(OpaRequest opaRequest) {
    ResponseEntity<OpaResult> response = restTemplate.postForEntity("/v1/data/single", opaRequest, OpaResult.class );

    if(!response.hasBody()) {
      return false;
    }

    log.info(String.valueOf(response.getBody()));

    return response.getBody().result().allow();
  }
}
```

### Stretch: Multiple resources
- Not a high priority since the **Record Overwrite permissions is not high priority**
#### (1): OPA Partial evaluation and custom repository that extends `MongoRepository`
- Inspiration: https://blog.openpolicyagent.org/write-policy-in-opa-enforce-policy-in-sql-d9d24db93bf4
- Some downsides by using this library
  - Not actively managed
  - Tight coupling with mongo (MQL) and opa (Rego) languages
- WIP: Problems integrating with library
  - `MongoRepository` Bean can be configured via `EnableMongoRepository`
  - Unable to instantiate Repository beans that use base `MongoRepository` interface  


####  (2) AOP which intercepts findAll(predicate, pageable), queries Repository
- Assume that already passed the short-circuit evaluation:
  - Means that there already exists a record that has "overwritten" record permissions
- `AuthAspect` as a simple PoC that this can be done. however, only covers a subset of the scenarios:
  1. PROFILE_ROLE granted, record overwrite ALLOWED 
  2. PROFILE_ROLE granted, record overwrite DENIED 
  3. PROFILE_ROLE not granted, record overwrite ALLOWED 
  4. PROFILE_ROLE not granted, record overwrite DENIED - no need to eval
- Enhancements possible:
  - A way to handle this generically
  - (Action, permission) by class method?
  - return `joinPoint.proceed` response?

#### (3) Spring Data mongo
- Custom Repository implementation (https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongodb.repositories.queries) - similar to (1) 
- Event Callbacks https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongodb.mapping-usage.events 
  - `AfterLoad`? - may need to do some manipulation before get authz