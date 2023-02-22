package com.example.auth.authz.taxonomy;

/**
 * This enum contains all records (collections)
 * Since it is a static list, it is stored as an enum.
 * <p>
 * The naming convention is the FACET_COLLECTION
 */
public enum Record implements RecordMapping {
    CAREER_HISTORY {
        @Override
        public Facet getFacetByRecord() {
            return Facet.CAREER;
        }

        @Override
        public String getCollectionName() {
            return "careerHistory";
        }
    },
    CAREER_REVIEW {
        @Override
        public Facet getFacetByRecord() {
            return Facet.CAREER;
        }

        @Override
        public String getCollectionName() {
            return "careerReview";
        }
    }
}
