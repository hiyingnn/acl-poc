package com.example.auth.career.history.domain;

import com.example.auth.authz.authzv1.recordoverwrite.RecordOverwriteAcl;

import java.util.List;

public interface RecordOverwrite {

    List<RecordOverwriteAcl> getRecordAcls();
}
