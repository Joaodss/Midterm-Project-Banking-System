package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.repository.user.ThirdPartyRepository;
import com.ironhack.midterm.service.user.RoleService;
import com.ironhack.midterm.service.user.ThirdPartyService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

  @Autowired
  private ThirdPartyRepository thirdPartyRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private RoleService roleService;


  // ======================================== GET THIRD PARTY Methods ========================================
  @Override
  public List<ThirdParty> getAll() {
    return thirdPartyRepository.findAll();
  }


  // ======================================== SAVE THIRD PARTY Methods ========================================
  @Override
  public void newUser(UserDTO thirdParty) throws EntityExistsException {
    // Check if username already exists
    if (userService.isUsernamePresent(thirdParty.getUsername()))
      throw new EntityExistsException("Username already exists.");

    ThirdParty tp = new ThirdParty(thirdParty.getUsername(), thirdParty.getPassword(), thirdParty.getName());

    // Set "THIRD_PARTY" role
    Optional<Role> userRole = roleService.getByName("THIRD_PARTY");
    if (userRole.isPresent()) {
      tp.getRoles().add(userRole.get());
    } else {
      roleService.newRole("THIRD_PARTY");
      Optional<Role> newUserRole = roleService.getByName("THIRD_PARTY");
      newUserRole.ifPresent(role -> tp.getRoles().add(role));
    }
    thirdPartyRepository.save(tp);
  }


  // ======================================== utils Methods ========================================
  @Override
  public boolean hasHashedKey(String hashedKey) {
    Optional<ThirdParty> thirdParty = thirdPartyRepository.findByHashedKey(hashedKey);
    return thirdParty.isPresent();
  }


}
