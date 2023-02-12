package com.example.auth.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SourceDTO {
  LocalDateTime dateObtained;
  ReferenceType referenceType;
  String comment;

}
