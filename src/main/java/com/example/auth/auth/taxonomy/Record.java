package com.example.auth.auth.taxonomy;

/**
 * This enum contains all records (collections)
 * Since it is a static list, it is stored as an enum.
 * <p>
 * The naming convention is the FACET_COLLECTION
 */
public enum Record implements RecordToFacetMapping {
  CAREER_HISTORY {
    @Override
    public Facet getFacetByRecord() {
      return Facet.CAREER;
    }
  },
  CAREER_REVIEW {
    @Override
    public Facet getFacetByRecord() {
      return Facet.CAREER;
    }
  }
}
