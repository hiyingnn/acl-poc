package com.example.auth.career.history.dto;

import com.example.auth.common.ReferencesDTO;
import com.example.auth.config.ValidReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@ValidReference
public class AppointmentDTO extends ReferencesDTO {
  String position;
  String rank;

  @Override
  public Set<String> getMandatoryReferences() {
    return Set.of(Fields.position);
  }

  @Override
  public Set<String> getOptionalReferences() {
    return Set.of(Fields.rank);
  }

}
