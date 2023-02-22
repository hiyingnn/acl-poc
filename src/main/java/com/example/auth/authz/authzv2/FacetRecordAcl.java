package com.example.auth.authz.authzv2;

import com.example.auth.authz.role.Effect;
import com.example.auth.authz.role.Permission;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Builder(toBuilder = true)
@TypeAlias("facetRecordAcl")
@Document(collection = "facetRecordAcl")
public class FacetRecordAcl {
    @MongoId
    String id;
    String identifier;
    UserAndGroup userAndGroup;
    Resource resource;
    Long profileId;
    boolean isPermissionsDerived;
    List<Permission> permissions;
    Effect effect;
}
