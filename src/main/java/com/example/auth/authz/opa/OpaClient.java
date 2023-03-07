package com.example.auth.authz.opa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OpaClient {
  private final RestTemplate restTemplate;

  public OpaClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
            .rootUri("http://localhost:8181")
            .build();
  }

  public boolean isAllowed(OpaRequest opaRequest) {
    ResponseEntity<OpaResponse> response = restTemplate.postForEntity("/v1/data/authz/allow", opaRequest, OpaResponse.class );

    if(!response.hasBody()) {
      return false;
    }

    log.info(String.valueOf(response.getBody()));

    return response.getBody().allow();
  }
}
