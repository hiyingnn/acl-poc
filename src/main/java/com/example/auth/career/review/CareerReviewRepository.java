package com.example.auth.career.review;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CareerReviewRepository extends MongoRepository<CareerReview, String> {
}
