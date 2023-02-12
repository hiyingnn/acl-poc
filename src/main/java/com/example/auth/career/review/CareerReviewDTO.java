package com.example.auth.career.review;

import lombok.Data;

@Data
public class CareerReviewDTO {
  String id;
  Long profileId;
  Long careerId;

  String review;
  Long version;
}
