package com.example.auth.authz.authzv1.recordoverwrite;

import com.example.auth.authz.role.Action;
import com.example.auth.authz.CustomUser;
import com.example.auth.authz.role.Effect;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.taxonomy.Facet;
import com.example.auth.authz.taxonomy.Record;
import com.example.auth.career.history.domain.RecordOverwrite;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostAuthRecordAuthorizationService {

    public boolean hasPermissionToActOnResource(CustomUser customUser, Action action, Record resource, Optional<RecordOverwrite> recordOverwriteOptional) {
        if (recordOverwriteOptional.isEmpty()) return true;
        RecordOverwrite recordOverwrite = recordOverwriteOptional.get();

        Facet facet = resource.getFacetByRecord();
        Permission permission = facet.getPermissionByActionAndResource(action);

        Optional<Effect> effect = recordOverwrite.getRecordAcls().stream().filter(
                recordAcl -> recordAcl.getUser().equals(customUser.getUsername())
                        && recordAcl.getPermission().equals(permission)
        ).map(RecordOverwriteAcl::getEffect).findFirst();

        return effect.isEmpty() || effect.get().equals(Effect.ALLOW);
    }
}
