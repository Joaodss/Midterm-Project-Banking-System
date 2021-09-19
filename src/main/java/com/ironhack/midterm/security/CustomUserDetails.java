package com.ironhack.midterm.security;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dao.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class CustomUserDetails implements UserDetails {

  private final User user;

  // ============================== Constructor ==============================
  public CustomUserDetails(User user) {
    this.user = user;
  }


  // ============================== Methods ==============================
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new HashSet<>();
    for (Role role : user.getRoles()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }
    return authorities;
  }

  public String getPassword() {
    return user.getPassword();
  }

  public String getUsername() {
    return user.getUsername();
  }

  public boolean isAccountNonExpired() {
    return true;
  }

  public boolean isAccountNonLocked() {
    return true;
  }

  public boolean isCredentialsNonExpired() {
    return true;
  }

  public boolean isEnabled() {
    return true;
  }


}
