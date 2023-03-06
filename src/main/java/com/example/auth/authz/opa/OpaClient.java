package com.example.auth.authz.opa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpaClient {
  private final RestTemplate restTemplate;

  public boolean isAllowed(Object data, Object input) {
//    restTemplate.postForObject("http://localhost:8181/v1/data/authz/allow", _,_)
    return true;
  }
}
