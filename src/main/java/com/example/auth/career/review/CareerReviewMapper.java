package com.example.auth.career.review;

import org.mapstruct.Mapper;

@Mapper
public interface CareerReviewMapper {
  CareerReview toCareerGoal(CareerReviewDTO careerReviewDTO);

  CareerReviewDTO toCareerGoalDTO(CareerReview careerReview);
}
