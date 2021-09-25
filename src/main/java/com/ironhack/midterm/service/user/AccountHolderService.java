package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.dto.UserAccountHolderDTO;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface AccountHolderService {

  // ======================================== get Methods ========================================
  List<AccountHolder> getAll();

  // ======================================== new Methods ========================================
  void newUser(UserAccountHolderDTO accountHolder) throws EntityExistsException;

  // ======================================== utils Methods ========================================
  AccountHolder[] findAccountHolders(AccountDTO account) throws EntityNotFoundException, IllegalArgumentException;


}
