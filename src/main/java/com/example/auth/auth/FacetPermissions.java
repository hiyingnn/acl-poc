package com.example.auth.auth;

import com.example.auth.auth.taxonomy.Record;

import java.util.List;

public interface FacetPermissions {
  Permission getPermissionByActionAndResource(Action action) throws IllegalArgumentException;

  default List<Record> getRecords() {
    return List.of();
  }
}
