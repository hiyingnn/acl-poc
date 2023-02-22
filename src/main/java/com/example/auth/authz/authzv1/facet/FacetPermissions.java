package com.example.auth.authz.authzv1.facet;

import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Permission;
import com.example.auth.authz.taxonomy.Record;

import java.util.List;

public interface FacetPermissions {
  Permission getPermissionByActionAndResource(Action action) throws IllegalArgumentException;

  default List<Record> getRecords() {
    return List.of();
  }
}
