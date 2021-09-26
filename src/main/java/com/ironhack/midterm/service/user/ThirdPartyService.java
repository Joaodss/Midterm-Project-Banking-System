package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dto.UserDTO;

import javax.persistence.EntityExistsException;
import java.util.List;

public interface ThirdPartyService {

  // ======================================== get Methods ========================================
  List<ThirdParty> getAll();


  // ======================================== new Methods ========================================
  void newUser(UserDTO thirdParty) throws EntityExistsException;


  // ======================================== utils Methods ========================================
  boolean hasHashedKey(String hashedKey);

}
