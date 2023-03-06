package com.example.auth.authz.opa;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpaService {

  private final OpaClient opaClient;

  public boolean allow(String action, String resource, String resourceId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    return opaClient.isAllowed();
    return true;
  }
}
