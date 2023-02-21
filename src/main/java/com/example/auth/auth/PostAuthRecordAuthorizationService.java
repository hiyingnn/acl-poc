package com.example.auth.auth;

import com.example.auth.auth.taxonomy.Facet;
import com.example.auth.auth.taxonomy.Record;
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
