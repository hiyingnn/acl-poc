package com.example.auth.career.history.domain;

import com.example.auth.auth.RecordOverwriteAcl;

import java.util.List;

public interface RecordOverwrite {
    List<RecordOverwriteAcl> getRecordAcls();
}
