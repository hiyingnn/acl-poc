package com.example.auth.authz.authzv2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAndGroup {

    UserAndGroupType type;
    String name;
}
