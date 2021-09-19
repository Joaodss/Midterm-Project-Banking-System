package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountHolderDTO;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

public interface AccountHolderService {

  // ======================================== GET USERS Methods ========================================
  List<AccountHolder> getAll();


  // ======================================== ADD USERS Methods ========================================
  void newUser(AccountHolderDTO accountHolder) throws InstanceAlreadyExistsException;


}
