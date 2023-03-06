package com.example.auth.authz.opa;

import com.example.auth.authz.authzv2.RecordAcl;

import java.util.List;

public record DataInput(List<RecordAcl> profileRoles,
                        List<RecordAcl> recordPermissions) {
}
