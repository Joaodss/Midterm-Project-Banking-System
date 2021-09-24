package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface UserService {

  // ======================================== GET Methods ========================================
  List<User> getAll();

  User getById(Long id) throws InstanceNotFoundException;

  boolean isUsernamePresent(String username);

  User getByUsername(String username) throws InstanceNotFoundException;

  void changePassword(String username, UserEditPasswordDTO userPassword) throws InstanceNotFoundException;

  void edit(String username, UserEditDTO user) throws InstanceNotFoundException;


}
