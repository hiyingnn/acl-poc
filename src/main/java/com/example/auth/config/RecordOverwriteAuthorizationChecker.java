package com.example.auth.config;

import com.example.auth.auth.*;
import com.example.auth.auth.taxonomy.Record;
import com.example.auth.career.history.domain.RecordOverwrite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecordOverwriteAuthorizationChecker {
    private final MongoTemplate mongoTemplate;

    public RecordOverwriteResult hasPermissionToActOnResource(CustomUser customUser, Permission permission, Long profileId, Record resource, String resourceId) {
        String collectionName = resource.getCollectionName();

        Criteria overwritePermissionCriteria = Criteria.where("user")
                .is(customUser.getUsername())
                .and("permission").is(permission);

        // Storing record acl in actual document: requires complex querying and need to filter array of elements again
        Criteria elemMatchCriteria = Criteria.where("recordAcls").elemMatch(overwritePermissionCriteria);
        // To investigate: Criteria query by id automatically wraps query with oid, requiring document to annotate id as @MongoId(FieldType.OBJECT_ID)
        // Check if MongoConverter/overriding methods are required
        Criteria recordEqualCriteria = Criteria.where("profileId").is(profileId).and("_id").is(resourceId);
        Criteria criteria = new Criteria().andOperator(elemMatchCriteria, recordEqualCriteria);

        var recordOverwrite = mongoTemplate.find(Query.query(criteria), RecordOverwrite.class, collectionName);

        log.info("record overwritten {}", recordOverwrite);
        if (recordOverwrite.isEmpty()) return RecordOverwriteResult.NOT_OVERWRITTEN;

        Optional<Effect> recordOverwriteResult = recordOverwrite.get(0).getRecordAcls().stream().filter(
                record -> record.getPermission().equals(permission)
                        && record.getUser().equals(customUser.getUsername())
        ).map(RecordOverwriteAcl::getEffect).findFirst();

        if (recordOverwriteResult.isEmpty()) {
            return RecordOverwriteResult.NOT_OVERWRITTEN;
        } else if (recordOverwriteResult.get().equals(Effect.GRANT)) {
            return RecordOverwriteResult.GRANTED;
        } else if (recordOverwriteResult.get().equals(Effect.REVOKE)) {
            return RecordOverwriteResult.REVOKED;
        }

        return RecordOverwriteResult.REVOKED;
    }
}
