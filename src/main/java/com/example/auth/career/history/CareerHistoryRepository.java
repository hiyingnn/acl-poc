package com.example.auth.career.history;

import com.example.auth.career.history.domain.CareerHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerHistoryRepository extends MongoRepository<CareerHistory, String>, QuerydslPredicateExecutor<CareerHistory> {
}
