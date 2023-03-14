package com.example.auth.aop;

import com.example.auth.authz.authzv2.RecordAcl;
import com.example.auth.authz.opa.AuthService;
import com.example.auth.authz.role.Action;
import com.example.auth.authz.role.Effect;
import com.example.auth.authz.taxonomy.Record;

import com.example.auth.career.history.domain.QCareerHistory;
import com.example.auth.career.history.dto.CareerHistoryDTO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Aspect
@Slf4j
public class AuthAspect {
    private final AuthService authService;

    @Around(value="execution(public * com.example.auth.career.history.*Service.getAllRecords(..)) && args(profileId, predicate, pageable) ")
    public Page<CareerHistoryDTO> aroundAdvice(ProceedingJoinPoint joinPoint, Long profileId, Predicate predicate, Pageable pageable) throws Throwable {
       log.info("In Around Aspect");

        List<RecordAcl> recordAcls = authService.getRecordOverwritePermissions(Action.READ, profileId, Record.CAREER_HISTORY);
        List<String> denyRecordIds = recordAcls.stream().filter(acl -> acl.getEffect().equals(Effect.DENY)).map(acl -> acl.getResource().getRecordId()).toList();
        List<String> allowRecordIds = recordAcls.stream().filter(acl -> acl.getEffect().equals(Effect.ALLOW)).map(acl -> acl.getResource().getRecordId()).toList();

        Predicate newPredicate = ExpressionUtils.allOf(QCareerHistory.careerHistory.id.notIn(denyRecordIds),predicate);
        return (Page<CareerHistoryDTO>) joinPoint.proceed(new Object[]{profileId, newPredicate, pageable });

    }
}