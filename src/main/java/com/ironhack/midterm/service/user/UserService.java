package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.User;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface UserService {

  // ======================================== GET Methods ========================================
  List<User> getAll();

  User getById(Long id) throws InstanceNotFoundException;

  boolean isUsernamePresent(String username);

  User getByUsername(String username) throws InstanceNotFoundException;


}
