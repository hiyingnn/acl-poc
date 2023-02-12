package com.example.auth.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import com.example.auth.common.ReferenceDTO;
import com.example.auth.common.ReferencesDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ReferenceValidator implements ConstraintValidator<ValidReference, Object> {
    @Autowired
    ObjectMapper objectMapper;

    Set<String> mandatoryReferences = new HashSet<>();
    Set<String> optionalReferences = new HashSet<>();
    Set<String> supportedReferences = new HashSet<>();

    @Override
    public void initialize(ValidReference constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * TODO: add custom error messages
     *
     * @param dto
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext constraintValidatorContext) {

        ReferencesDTO refDTO = (ReferencesDTO) dto;

        mandatoryReferences = refDTO.getMandatoryReferences();
        optionalReferences = refDTO.getOptionalReferences();
        supportedReferences = refDTO.getSupportedReferences();

        boolean isAllSupported = isAllSupported(refDTO, constraintValidatorContext);
        boolean isAllMandatory = isAllMandatoryPresent(refDTO, constraintValidatorContext);

        return isAllSupported && isAllMandatory;
    }


    /**
     * Check if only supported references exist (i.e., both Mandatory and Optional!)
     */
    public boolean isAllSupported(ReferencesDTO referencesDTO, ConstraintValidatorContext constraintContext) {
        List<String> referenceNotSupportedList
                = referencesDTO.getReferences().stream()
                .map(ReferenceDTO::getField)
                .filter(refField -> !supportedReferences.contains(refField))
                .toList();

        log.error("Unsupported present {}", referenceNotSupportedList);

        if (!referenceNotSupportedList.isEmpty()) {
            String constraintValidation = String.format("Unsupported reference present for %s: %s", referencesDTO.getClass().getSimpleName(), referenceNotSupportedList);
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(
                            constraintValidation
                    ).addPropertyNode("references")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Check if mandatory references (matching content) exists
     */
    public boolean isAllMandatoryPresent(ReferencesDTO referencesDTO, ConstraintValidatorContext constraintContext) {

        Map<String, List<String>> mandatoryReferenceNotPresent = new HashMap<>();

        for (String mandatoryRef : mandatoryReferences) {
            Set<String> contentFromRef = referencesDTO.getContentSetByField(mandatoryRef);

            if (referencesDTO.isAttributedToObjectInferred() && mandatoryRef.equals(ReferencesDTO.ATTRIBUTE_TO_OBJ)) {
                if (contentFromRef.isEmpty()) {
                    mandatoryReferenceNotPresent.put(mandatoryRef, List.of(mandatoryRef));
                }
                continue;
            }

            try {
                String jsonString = objectMapper.writeValueAsString(referencesDTO);

                Field field = referencesDTO.getClass().getDeclaredField(mandatoryRef);

                if (field.getType().isAssignableFrom(List.class)) {
                    List<String> contentList = JsonPath.parse(jsonString)
                            .read(String.format("$.%s[*]", mandatoryRef));

                    List<String> mandatoryContentAbsentInMulti = contentList
                            .stream()
                            .filter(content -> !contentFromRef.contains(content)).toList();

                    if (!mandatoryContentAbsentInMulti.isEmpty()) {
                        mandatoryReferenceNotPresent.put(mandatoryRef, mandatoryContentAbsentInMulti);
                    }

                } else {
                    String content = JsonPath.parse(jsonString)
                            .read(String.format("$.%s", mandatoryRef));

                    boolean mandatoryContentAbsentInSingle = !contentFromRef.contains(content);

                    if (mandatoryContentAbsentInSingle) {
                        mandatoryReferenceNotPresent.put(mandatoryRef, List.of(content));
                        log.error("Reference not present {} in single", mandatoryRef);
                    }
                }

            } catch (JsonProcessingException | NoSuchFieldException e) {
                return false;
            }
        }

        if (!mandatoryReferenceNotPresent.isEmpty()) {
            String constraintValidation = String.format("Mandatory reference not present for %s: %s", referencesDTO.getClass().getSimpleName(), mandatoryReferenceNotPresent);
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(
                            constraintValidation
                    ).addPropertyNode("references")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
