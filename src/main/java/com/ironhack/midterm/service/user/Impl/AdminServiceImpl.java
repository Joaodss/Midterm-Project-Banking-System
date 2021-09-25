package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.repository.user.AdminRepository;
import com.ironhack.midterm.service.user.AdminService;
import com.ironhack.midterm.service.user.RoleService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private RoleService roleService;


  // ======================================== GET ADMINS Methods ========================================
  public List<Admin> getAll() {
    return adminRepository.findAll();
  }


  // ======================================== ADD ADMINS Methods ========================================
  public void newUser(UserDTO admin) throws EntityExistsException {
    // Check if username already exists
    if (userService.isUsernamePresent(admin.getUsername()))
      throw new EntityExistsException("Username already exists.");

    Admin a = new Admin(admin.getUsername(), admin.getPassword(), admin.getName());

    // Set "ADMIN" role
    Optional<Role> userRole = roleService.getByName("ADMIN");
    if (userRole.isPresent()) {
      a.getRoles().add(userRole.get());
    } else {
      roleService.newRole("ADMIN");
      Optional<Role> newUserRole = roleService.getByName("ADMIN");
      newUserRole.ifPresent(role -> a.getRoles().add(role));
    }
    adminRepository.save(a);
  }
}
