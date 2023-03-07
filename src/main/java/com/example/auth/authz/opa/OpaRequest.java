package com.example.auth.authz.opa;

public record OpaRequest(
        DataInput data,
        RequestInput request
) {
}
