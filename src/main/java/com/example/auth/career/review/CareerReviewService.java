package com.example.auth.career.review;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareerReviewService {

  private final CareerReviewRepository careerReviewRepository;
  private final CareerReviewMapper careerReviewMapper;

  public CareerReviewDTO addRecord(CareerReviewDTO careerReviewDTO) {
    CareerReview CareerReview = careerReviewMapper.toCareerGoal(careerReviewDTO);
    CareerReview createdCareerReview = careerReviewRepository.save(CareerReview);

    CareerReviewDTO createdCareerReviewDTO = careerReviewMapper.toCareerGoalDTO(createdCareerReview);

    return createdCareerReviewDTO;
  }

  public List<CareerReviewDTO> getAllRecords() {
    return careerReviewRepository
      .findAll()
      .stream()
      .map(careerReviewMapper::toCareerGoalDTO)
      .toList();
  }

  public Optional<CareerReviewDTO> getRecordById(String id) {
    Optional<CareerReview> CareerGoal = careerReviewRepository.findById(id);
    return CareerGoal.map(careerReviewMapper::toCareerGoalDTO);
  }

  public CareerReviewDTO updateRecord(String id, CareerReviewDTO newCareerReviewDTO) {
    Optional<CareerReview> CareerGoal = careerReviewRepository.findById(id);

    if (CareerGoal.isEmpty()) throw new IllegalArgumentException("Not found");

    CareerReview newCareerReview = careerReviewMapper.toCareerGoal(newCareerReviewDTO);
    CareerReview createdCareerReview = careerReviewRepository.save(newCareerReview);


    CareerReviewDTO createdCareerReviewDTO = careerReviewMapper.toCareerGoalDTO(createdCareerReview);

    return createdCareerReviewDTO;
  }


}
