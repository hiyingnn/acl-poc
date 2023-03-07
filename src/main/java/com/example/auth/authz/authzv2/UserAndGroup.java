package com.example.auth.authz.authzv2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAndGroup {
    enum UserAndGroupType{
        TEAM,
        USER
    };
    UserAndGroupType type;
    String name;
}
