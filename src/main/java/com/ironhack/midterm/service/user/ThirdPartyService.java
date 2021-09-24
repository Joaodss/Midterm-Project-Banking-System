package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dto.ThirdPartyDTO;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

public interface ThirdPartyService {

  // ======================================== GET USERS Methods ========================================
  List<ThirdParty> getAll();



  // ======================================== ADD USERS Methods ========================================
  void newUser(ThirdPartyDTO thirdParty) throws InstanceAlreadyExistsException;

  boolean hasHashedKey(String hashedKey);
}
