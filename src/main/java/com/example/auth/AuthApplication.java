package com.example.auth;

import com.example.auth.auth.ProfileAcl;
import com.example.auth.auth.ProfileAclRepository;
import com.example.auth.auth.Role;
import com.example.auth.auth.TeamToRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
public class AuthApplication implements CommandLineRunner {
  @Autowired
  ProfileAclRepository profileAclRepository;

  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    ProfileAcl p = ProfileAcl
      .builder()
      .profileId(1L)
      .teamToRoleList(List.of(new TeamToRole("STARFRUIT", Role.R_CAREER)))
      .build();
    profileAclRepository.save(p);
  }
}
