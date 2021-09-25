package com.ironhack.midterm.service.user;


import com.ironhack.midterm.dao.user.Role;

import java.util.Optional;

public interface RoleService {

  // ======================================== get Methods ========================================
  Optional<Role> getByName(String name);

  // ======================================== new Methods ========================================
  void newRole(String name);

}
