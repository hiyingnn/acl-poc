package com.example.auth.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReferenceResolver {
    @Autowired
    ObjectMapper objectMapper;

    @AfterMapping
    public <T extends ReferencesDTO, S extends References.ReferencesBuilder<?, ?>> void syncReference(T baseDTO, @MappingTarget S builder) {
        List<Reference> inSyncReference = new ArrayList<>();
        Set<String> supportedReferences = baseDTO.getSupportedReferences();

        Map<String, String> fieldDTOtoFieldDOMapping = baseDTO.getFieldDTOtoFieldDOMapping();
        List<ReferenceDTO> references = baseDTO
                .getReferences()
                .stream()
                .filter(ref -> supportedReferences.contains(ref.getField()))
                .toList();

        try {
            String jsonString = objectMapper.writeValueAsString(baseDTO);

            for (ReferenceDTO ref : references) {
                String field = ref.getField();
                String convertedField = fieldDTOtoFieldDOMapping.getOrDefault(field, field);

                log.info("JSON PATH {}", field);

                if (baseDTO.isAttributedToObjectInferred()) {
                    inSyncReference.add(referenceDTOtoReference(ref, convertedField));
                    continue;
                }

                Field declaredField = baseDTO.getClass().getDeclaredField(field);

                if (declaredField.getType().isAssignableFrom(List.class)) {
                    List<String> contentList = JsonPath.parse(jsonString)
                            .read(String.format("$.%s[*]", field));
                    if (contentList != null && contentList.contains(ref.getContent())) {
                        inSyncReference.add(referenceDTOtoReference(ref, convertedField));
                    }
                } else {

                    String content = JsonPath.parse(jsonString)
                            .read(String.format("$.%s", field));

                    if (content != null && content.equals(ref.getContent())) {
                        inSyncReference.add(referenceDTOtoReference(ref, convertedField));
                    }
                }
            }

        } catch (JsonProcessingException | NoSuchFieldException e) {
            e.printStackTrace();
            inSyncReference = references.stream().map(ref -> referenceDTOtoReference(ref, ref.getField())).toList();
        }

        builder.references(groupByFieldContent(inSyncReference));
    }

    public List<Reference> groupByFieldContent(List<Reference> references) {

        var fieldContentToSources = references.stream()
                .collect(Collectors.groupingBy(
                        ref -> Pair.of(ref.getField(), ref.getContent()),
                        Collectors.flatMapping
                                (ref -> ref.getSources().stream(),
                                        Collectors.toList())));

        return fieldContentToSources.entrySet().stream().map(es -> {
            Pair<String, String> fieldContentPair = es.getKey();
            String field = fieldContentPair.getFirst();
            String content = fieldContentPair.getSecond();
            List<Source> sources = es.getValue();
            return Reference.builder()
                    .field(field)
                    .content(content)
                    .sources(sources)
                    .build();
        }).toList();

    }

    public Reference referenceDTOtoReference(ReferenceDTO referenceDTO, String field) {
        if (referenceDTO == null) {
            return null;
        }

        return Reference.builder()
                .field(field)
                .content(referenceDTO.getContent())
                .sources(sourceDTOListSourceList(referenceDTO.sources))
                .build();
    }

    public List<Source> sourceDTOListSourceList(List<SourceDTO> list) {
        if (list == null) {
            return null;
        } else {
            List<Source> list1 = new ArrayList(list.size());
            Iterator var3 = list.iterator();

            while (var3.hasNext()) {
                SourceDTO sourceDTO = (SourceDTO) var3.next();
                list1.add(this.map(sourceDTO));
            }

            return list1;
        }
    }

    public Source map(SourceDTO sourceDTO) {
        return Source
                .builder()
                .referenceType(sourceDTO.referenceType)
                .dateObtained(sourceDTO.dateObtained)
                .comment(sourceDTO.comment)
                .build();
    }

    @AfterMapping
    public <T extends References, S extends ReferencesDTO.ReferencesDTOBuilder<?, ?>> void syncReferenceDTO(T baseDTO, @MappingTarget S builder) {
        ReferencesDTO refDTO = builder.build();
        List<ReferenceDTO> inSyncReference = new ArrayList<>();

        Map<String, String> fieldDOtoFieldDTOMapping = refDTO.getFieldDOtoFieldDTOMapping();

        Set<String> supportedReferences = refDTO.getSupportedReferences();

        if (baseDTO.getReferences() == null || baseDTO.getReferences().isEmpty()) return;

        List<Reference> references = baseDTO
                .getReferences()
                .stream()
                .filter(ref -> supportedReferences.contains(fieldDOtoFieldDTOMapping.getOrDefault(ref.getField(), ref.getField())))
                .toList();

        try {
            String jsonString = objectMapper.writeValueAsString(baseDTO);

            for (Reference ref : references) {
                String field = ref.getField();

                String convertedField = fieldDOtoFieldDTOMapping.getOrDefault(field, field);
                log.info("JSON PATH {}", field);


                if (refDTO.isAttributedToObjectInferred()) {
                    inSyncReference.add(referenceToReferenceDTO(ref, convertedField));
                    continue;
                }

                Optional<Field> declaredField = recursivelyFindField(field, baseDTO);

                if (declaredField.isEmpty()) {
                    throw new RuntimeException("Invalid la");
                }

                if (declaredField.get().getType().isAssignableFrom(List.class)) {
                    List<String> contentList = JsonPath.parse(jsonString)
                            .read(String.format("$.%s[*]", field));
                    if (contentList != null && contentList.contains(ref.getContent())) {
                        inSyncReference.add(referenceToReferenceDTO(ref, convertedField));
                    }
                } else {

                    String content = JsonPath.parse(jsonString)
                            .read(String.format("$.%s", field));

                    if (content != null && content.equals(ref.getContent())) {
                        inSyncReference.add(referenceToReferenceDTO(ref, convertedField));
                    }
                }
            }

        } catch (JsonProcessingException | NoSuchFieldException e) {
            e.printStackTrace();
            inSyncReference = references.stream().map(ref -> referenceToReferenceDTO(ref, ref.getField())).toList();
        }

        builder.references(inSyncReference);
    }

    public <T extends References> Optional<Field> recursivelyFindField(String jsonPath, T baseDTO) throws NoSuchFieldException {
        String[] fieldPaths = jsonPath.split("\\.");

        Field field = null;
        Class<?> clazz = baseDTO.getClass();
        for (String fieldPath : fieldPaths) {
            field = clazz.getDeclaredField(fieldPath);
            clazz = field.getType();
        }
        return Optional.ofNullable(field);
    }

    public ReferenceDTO referenceToReferenceDTO(Reference reference, String field) {
        if (reference == null) {
            return null;
        }

        return ReferenceDTO.builder()
                .field(field)
                .content(reference.getContent())
                .sources(sourceListToSourceDTOList(reference.sources))
                .build();
    }

    public List<SourceDTO> sourceListToSourceDTOList(List<Source> list) {
        if (list == null) {
            return null;
        } else {
            List<SourceDTO> list1 = new ArrayList(list.size());
            Iterator var3 = list.iterator();

            while (var3.hasNext()) {
                Source source = (Source) var3.next();
                list1.add(this.map(source));
            }

            return list1;
        }
    }

    public SourceDTO map(Source source) {
        return SourceDTO
                .builder()
                .referenceType(source.referenceType)
                .dateObtained(source.dateObtained)
                .comment(source.comment)
                .build();
    }
}
