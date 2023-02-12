package com.example.auth.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@NoArgsConstructor
public abstract class ReferencesDTO {

    public static final String ATTRIBUTE_TO_OBJ = "*";

    List<ReferenceDTO> references;

    public Set<String> getContentSetByField(String field) {
        return references
                .stream()
                .filter(ref -> ref.getField().equals(field))
                .map(ReferenceDTO::getContent)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public final Map<String, String> getFieldDOtoFieldDTOMapping() {
        return getFieldDTOtoFieldDOMapping()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @JsonIgnore
    public Map<String, String> getFieldDTOtoFieldDOMapping() {
        return new HashMap<>();
    }

    @JsonIgnore
    public final boolean isAttributedToObjectInferred() {
        Set<String> attributeToObj = Set.of(ATTRIBUTE_TO_OBJ);
        return getMandatoryReferences().equals(attributeToObj)
                || getOptionalReferences().equals(attributeToObj);
    }

    @JsonIgnore
    public abstract Set<String> getMandatoryReferences();

    @JsonIgnore
    public abstract Set<String> getOptionalReferences();

    @JsonIgnore
    public Set<String> getSupportedReferences() {
        Set<String> allSupported = new HashSet<>(getOptionalReferences());
        allSupported.addAll(getMandatoryReferences());
        return allSupported;
    }

}
