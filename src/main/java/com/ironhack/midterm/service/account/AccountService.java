package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dto.AccountEditDTO;

import java.util.List;

public interface AccountService {

  // ======================================== get Methods ========================================
  List<Account> getAll();

  List<Account> getAllByUsername(String username);

  Account getById(Long id);

  // ======================================== edit Methods ========================================
  void edit(long id, AccountEditDTO accountEdit);

  // ======================================== utils Methods ========================================
  void save(Account account);

  void freezeAccount(long id);

  void updateBalance(Account account);

  boolean hasAccount(Long id);

}
