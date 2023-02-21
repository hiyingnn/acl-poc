package com.example.auth.auth;

import com.example.auth.auth.CustomUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final Map<String, CustomUser> users = new HashMap();

  public CustomUserDetailsService() {
    CustomUser alice = CustomUser.builder()
      .password("{noop}password")
      .username("alice")
      .teams(List.of("APPLE", "BANANA"))
      .build();

    CustomUser bob = CustomUser.builder()
      .password("{noop}password")
      .username("bob")
      .teams(List.of("STARFRUIT", "ORANGE"))
      .build();

    users.put(alice.getUsername(), alice);
    users.put(bob.getUsername(), bob);
  }


  @Override
  public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
    if (!users.containsKey(username)) {
      throw new UsernameNotFoundException("Username not found");
    }

    return users.get(username);
  }
}
