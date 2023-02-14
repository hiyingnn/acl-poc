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
    CareerReview careerReview = careerReviewMapper.toCareerGoal(careerReviewDTO);
    CareerReview createdCareerReview = careerReviewRepository.save(careerReview);

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
    Optional<CareerReview> careerReview = careerReviewRepository.findById(id);
    return careerReview.map(careerReviewMapper::toCareerGoalDTO);
  }

  public CareerReviewDTO updateRecord(String id, CareerReviewDTO newCareerReviewDTO) {
    Optional<CareerReview> careerReview = careerReviewRepository.findById(id);

    if (careerReview.isEmpty()) throw new IllegalArgumentException("Not found");

    CareerReview newCareerReview = careerReviewMapper.toCareerGoal(newCareerReviewDTO);
    CareerReview createdCareerReview = careerReviewRepository.save(newCareerReview);


    CareerReviewDTO createdCareerReviewDTO = careerReviewMapper.toCareerGoalDTO(createdCareerReview);

    return createdCareerReviewDTO;
  }

  public void deleteRecord(String id) {
    Optional<CareerReview> existingCareerReview = careerReviewRepository.findById(id);

    if (existingCareerReview.isEmpty()) throw new IllegalArgumentException("Not found");

    careerReviewRepository.deleteById(id);
  }


}
