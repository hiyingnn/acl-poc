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
public class CertificationToObjDTO extends ReferencesDTO {
  String name;
  String issuedBy;


  @Override
  public Set<String> getMandatoryReferences() {
    return Set.of(ATTRIBUTE_TO_OBJ);
  }

  @Override
  public Set<String> getOptionalReferences() {
    return Set.of();
  }
}
