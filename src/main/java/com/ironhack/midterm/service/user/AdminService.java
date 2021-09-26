package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dto.UserDTO;

import javax.persistence.EntityExistsException;
import java.util.List;

public interface AdminService {

  // ======================================== get Methods ========================================
  List<Admin> getAll();


  // ======================================== new Methods ========================================
  void newUser(UserDTO admin) throws EntityExistsException;

}
