package com.example.auth.career.history.dto;

import com.example.auth.authz.authzv1.recordoverwrite.RecordOverwriteAclDto;
import com.example.auth.common.ReferencesDTO;
import com.example.auth.config.ValidReference;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@SuperBuilder
@NoArgsConstructor
@ValidReference
public class CareerHistoryDTO extends ReferencesDTO {
  String id;
  String company;

  Long profileId;
  Long careerId;

  @Valid AppointmentDTO appointment;
  String duration;
  String lastDrawnSalary;
  List<String> skills;

  List<@Valid CertificationToFieldDTO> certs;
  Long version;

  List<RecordOverwriteAclDto> recordAcls;


  @Override
  public Set<String> getMandatoryReferences() {
    return Set.of(Fields.company, Fields.skills);
  }

  @Override
  public Set<String> getOptionalReferences() {
    return Set.of(Fields.duration);
  }
}
