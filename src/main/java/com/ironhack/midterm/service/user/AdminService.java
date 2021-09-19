package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dto.AdminDTO;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

public interface AdminService {

  // ======================================== GET USERS Methods ========================================
  List<Admin> getAll();


  // ======================================== ADD USERS Methods ========================================
  void newUser(AdminDTO admin) throws InstanceAlreadyExistsException;

}
