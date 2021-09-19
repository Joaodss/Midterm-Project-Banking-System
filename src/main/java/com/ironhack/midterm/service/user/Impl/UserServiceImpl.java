package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.repository.user.UserRepository;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;


  // ======================================== GET Methods ========================================
  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User getById(Long id) throws InstanceNotFoundException {
    var user = userRepository.findById(id);
    if (user.isPresent()) return user.get();
    throw new InstanceNotFoundException();
  }

  public User getByUsername(String username) throws InstanceNotFoundException {
    var user = userRepository.findByUsername(username);
    if (user.isPresent()) return user.get();
    throw new InstanceNotFoundException();
  }

  public boolean isUsernamePresent(String username) {
    return userRepository.findByUsername(username).isPresent();
  }


  // ======================================== SAVE Methods ========================================


}
