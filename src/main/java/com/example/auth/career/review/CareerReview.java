package com.example.auth.career.review;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@TypeAlias("careerReview")
@Builder
@Document(collection = "career")
public class CareerReview {
  @MongoId
  String id;

  Long profileId;
  Long careerId;

  String review;

  @Version
  Long version;
}
