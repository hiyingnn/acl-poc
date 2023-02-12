package com.example.auth.auth;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class CustomUser implements UserDetails {

  List<String> teams;
  String username;
  String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return "password";
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  public boolean isCredentialsNonExpired() {
    return true;
  }

  public boolean isEnabled(){
     return true;
  }
}
