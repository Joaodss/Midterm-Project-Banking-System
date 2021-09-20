package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.dto.AccountHolderDTO;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

public interface AccountHolderService {

  // ======================================== GET USERS Methods ========================================
  List<AccountHolder> getAll();

  AccountHolder getByUsername(String username) throws InstanceNotFoundException;

  boolean isUsernamePresent(String username);


  // ======================================== ADD USERS Methods ========================================

  void newUser(AccountHolderDTO accountHolder) throws InstanceAlreadyExistsException;


  // ======================================== UTILS Methods ========================================
  AccountHolder[] getAccountHolders(AccountDTO account, AccountHolderService accountHolderService, UserService userService) throws InstanceNotFoundException, IllegalArgumentException;

}
