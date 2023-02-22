package com.example.auth.authz.authzv1;

import com.example.auth.authz.CustomUser;
import com.example.auth.career.history.CareerHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
  private CareerHistoryRepository careerHistoryRepository;

  @Override
  public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    AuthorizationManager.super.verify(authentication, object);
  }

  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    log.error("authentication {}", authentication.get().getPrincipal());
    var customUser = (CustomUser) authentication.get().getPrincipal();
    var u = object.getRequest().getRequestURI();


    return new AuthorizationDecision(true);
  }
}
