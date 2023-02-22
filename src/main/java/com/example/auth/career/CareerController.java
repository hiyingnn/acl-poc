package com.example.auth.career;


import com.example.auth.authz.CustomUser;
import com.example.auth.career.history.CareerHistoryService;
import com.example.auth.career.history.dto.CareerHistoryDTO;
import com.example.auth.career.review.CareerReviewDTO;
import com.example.auth.career.review.CareerReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/career")
@RequiredArgsConstructor
public class CareerController {

    private final CareerHistoryService careerHistoryService;
    private final CareerReviewService careerReviewService;

    @GetMapping("history")
    public List<CareerHistoryDTO> getAllRecords() {
        return careerHistoryService.getAllRecords();
    }

    @GetMapping("profile/{profileId}/history/{id}")
    public Optional<CareerHistoryDTO> getOneRecord(@AuthenticationPrincipal CustomUser user, @PathVariable Long profileId, @PathVariable String id) {
        return careerHistoryService.getRecordById(id);
    }

    @PostMapping("profile/{profileId}/history")
    public CareerHistoryDTO addRecord(@AuthenticationPrincipal CustomUser user, @RequestBody @Valid CareerHistoryDTO careerHistoryDTO, @PathVariable Long profileId) {
        return careerHistoryService.addRecord(careerHistoryDTO);
    }

    @PutMapping("profile/{profileId}/history/{id}")
    public CareerHistoryDTO updateRecord(@PathVariable String id, @RequestBody @Valid CareerHistoryDTO careerHistoryDTO, @PathVariable Long profileId) {
        return careerHistoryService.updateRecord(id, careerHistoryDTO);
    }

    @DeleteMapping("profile/{profileId}/history/{id}")
    public void deleteRecord(@PathVariable String id) {
        careerHistoryService.deleteRecord(id);
    }

    @GetMapping("review")
    public List<CareerReviewDTO> getAllReviewRecords() {
        return careerReviewService.getAllRecords();
    }

    @GetMapping("profile/{profileId}/review/{id}")
    public Optional<CareerReviewDTO> getOneReviewRecord(@AuthenticationPrincipal CustomUser user, @PathVariable Long profileId, @PathVariable String id) {
        return careerReviewService.getRecordById(id);
    }

    @PostMapping("profile/{profileId}/review")
    public CareerReviewDTO addReviewRecord(@RequestBody @Valid CareerReviewDTO careerReviewDTO, @PathVariable Long profileId) {
        return careerReviewService.addRecord(careerReviewDTO);
    }

    @PutMapping("profile/{profileId}/review/{id}")
    public CareerReviewDTO updateReviewRecord(@PathVariable String id, @RequestBody @Valid CareerReviewDTO careerReviewDTO, @PathVariable Long profileId) {
        return careerReviewService.updateRecord(id, careerReviewDTO);
    }

    @DeleteMapping("profile/{profileId}/review/{id}")
    public void deleteReviewRecord(@PathVariable String id) {
        careerReviewService.deleteRecord(id);
    }

}
