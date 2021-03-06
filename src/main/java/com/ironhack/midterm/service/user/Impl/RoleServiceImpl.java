package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.repository.user.RoleRepository;
import com.ironhack.midterm.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  // ======================================== GET Methods ========================================
  @Override
  public Optional<Role> getByName(String name) {
    return roleRepository.findByName(name);
  }


  // ======================================== SAVE Methods ========================================
  @Override
  public void newRole(String name) {
    roleRepository.save(new Role(name));
  }

  // ======================================== PUT Methods ========================================

  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================
}
