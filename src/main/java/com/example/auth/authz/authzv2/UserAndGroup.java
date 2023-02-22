package com.example.auth.authz.authzv2;

import lombok.Data;

@Data
public class UserAndGroup {
    enum UserAndGroupType{
        TEAM,
        USER
    };
    UserAndGroupType type;
    String name;
}
