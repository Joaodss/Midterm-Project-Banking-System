package com.ironhack.midterm.security.service;

import com.ironhack.midterm.repository.user.UserRepository;
import com.ironhack.midterm.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    var user = userRepository.findByUsername(s);
    if (user.isEmpty()) throw new UsernameNotFoundException("User does not exist.");
    return new CustomUserDetails(user.get());
  }


}
