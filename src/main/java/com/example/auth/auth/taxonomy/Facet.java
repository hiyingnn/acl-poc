package com.example.auth.auth.taxonomy;

import com.example.auth.auth.Action;
import com.example.auth.auth.FacetPermissions;
import com.example.auth.auth.Permission;

import java.util.List;

/**
 * This enum contains all facets (group of collections that are categorised based on theme )
 * Since it is a static list, it is stored as an enum.
 * <p>
 * getRecords method is overwritten as a way to denote which record belongs to a particular collection
 */
public enum Facet implements FacetPermissions {
  CAREER {
    @Override
    public Permission getPermissionByActionAndResource(Action action) throws IllegalArgumentException {
      if (action.equals(Action.READ)) {
        return Permission.R_CAREER;
      } else if (action.equals(Action.WRITE)) {
        return Permission.W_CAREER;
      }
      throw new IllegalArgumentException("Unreachaeble statement");
    }

    @Override
    public List<Record> getRecords() {
      return List.of(Record.CAREER_REVIEW, Record.CAREER_HISTORY);
    }

  }
}
