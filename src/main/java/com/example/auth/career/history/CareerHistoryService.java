package com.example.auth.career.history;

import com.example.auth.career.history.domain.CareerHistory;
import com.example.auth.career.history.domain.QCareerHistory;
import com.example.auth.career.history.dto.CareerHistoryDTO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CareerHistoryService {

  private final CareerHistoryRepository careerHistoryRepository;
  private final CareerHistoryMapper careerHistoryMapper;

  public CareerHistoryDTO addRecord(CareerHistoryDTO careerHistoryDTO) {
    CareerHistory careerHistory = careerHistoryMapper.toCareerHistory(careerHistoryDTO);
    CareerHistory createdCareerHistory = careerHistoryRepository.save(careerHistory);

    CareerHistoryDTO createdCareerHistoryDTO = careerHistoryMapper.toCareerHistoryDTO(createdCareerHistory);

    return createdCareerHistoryDTO;
  }

  public Page<CareerHistoryDTO> getAllRecords(Long profileId, Predicate predicate, Pageable pageable) {
    var composedPredicate = ExpressionUtils.allOf(predicate, QCareerHistory.careerHistory.profileId.eq(profileId));
    Page<CareerHistory> careerHistories = careerHistoryRepository.findAll(composedPredicate, pageable);
    log.info(careerHistories.toString());
    return careerHistories.map(careerHistoryMapper::toCareerHistoryDTO);
  }

  public Page<CareerHistoryDTO> getAllRecords(  Pageable pageable) {
    Page<CareerHistory> careerHistories = careerHistoryRepository.findAll( pageable);

    return careerHistories.map(careerHistoryMapper::toCareerHistoryDTO);
  }

  public Optional<CareerHistoryDTO> getRecordById(String id) {
    Optional<CareerHistory> careerHistory = careerHistoryRepository.findById(id);
    return careerHistory.map(careerHistoryMapper::toCareerHistoryDTO);
  }

  public CareerHistoryDTO updateRecord(String id, CareerHistoryDTO newCareerHistoryDTO) {
    Optional<CareerHistory> careerHistory = careerHistoryRepository.findById(id);

    if (careerHistory.isEmpty()) throw new IllegalArgumentException("Not found");

    CareerHistory newCareerHistory = careerHistoryMapper.toCareerHistory(newCareerHistoryDTO);
    CareerHistory createdCareerHistory = careerHistoryRepository.save(newCareerHistory);


    CareerHistoryDTO createdCareerHistoryDTO = careerHistoryMapper.toCareerHistoryDTO(createdCareerHistory);

    return createdCareerHistoryDTO;
  }

  public void deleteRecord(String id) {
    Optional<CareerHistory> existingCareerHistory = careerHistoryRepository.findById(id);

    if (existingCareerHistory.isEmpty()) throw new IllegalArgumentException("Not found");

    careerHistoryRepository.deleteById(id);
  }


}
