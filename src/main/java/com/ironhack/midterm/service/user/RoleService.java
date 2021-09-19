package com.ironhack.midterm.service.user;


import com.ironhack.midterm.dao.user.Role;

import java.util.Optional;

public interface RoleService {

  // ======================================== GET Methods ========================================
  boolean isRolePresent(String name);

  Optional<Role> getRoleByName(String name);

  // ======================================== SAVE Methods ========================================
  void addRole(String name);

  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
