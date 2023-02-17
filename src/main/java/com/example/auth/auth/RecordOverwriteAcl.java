package com.example.auth.auth;

import lombok.Data;

@Data
public class RecordOverwriteAcl {
    String user;
    Effect effect;
    Permission permission;
}
