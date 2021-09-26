package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dto.AccountEditDTO;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface AccountService {

  // ======================================== get Methods ========================================
  List<Account> getAll();

  List<Account> getAllByUsername(String username);

  Account getById(Long id) throws EntityNotFoundException;

  // ======================================== edit Methods ========================================
  void edit(long id, AccountEditDTO accountEdit) throws EntityNotFoundException;

  // ======================================== utils Methods ========================================
  void save(Account account);

  void freezeAccount(long id) throws EntityNotFoundException;

  void updateBalance(Account account);

  boolean hasAccount(Long id);

}
