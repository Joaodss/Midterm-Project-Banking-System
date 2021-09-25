package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface UserService {

  // ======================================== get Methods ========================================
  List<User> getAll();

  User getById(Long id) throws EntityNotFoundException;

  User getByUsername(String username) throws EntityNotFoundException;


  // ======================================== edit Methods ========================================
  void editPassword(String username, UserEditPasswordDTO userPassword) throws EntityNotFoundException;

  void edit(String username, UserEditDTO user) throws EntityNotFoundException;


  // ======================================== util Methods ========================================
  boolean isUsernamePresent(String username);

}
