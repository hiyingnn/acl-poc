package com.example.auth.career.history.domain;

import com.example.auth.common.References;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class Appointment extends References {
  String position;
  String rank;
}
