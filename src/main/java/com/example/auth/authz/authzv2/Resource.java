package com.example.auth.authz.authzv2;

import lombok.Data;

@Data
public class Resource {
    enum ResourceType{
        RECORD,
        ROLE
    };
    ResourceType type;
    String resourceId;
}
