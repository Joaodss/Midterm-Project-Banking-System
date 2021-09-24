package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dto.AccountEditDTO;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface AccountService {

  // ======================================== GET Methods ========================================
  List<Account> getAll();

  Account getById(Long id) throws InstanceNotFoundException;

  List<Account> getAllByUsername(String username);


  // ============================== Freeze Account ==============================
  void freezeAccount(long id) throws InstanceNotFoundException;

  void unFreezeAccount(long id) throws InstanceNotFoundException;


  // ============================== Save Account ==============================
  void save(Account account);

  void edit(long id, AccountEditDTO accountEdit) throws InstanceNotFoundException;

}
