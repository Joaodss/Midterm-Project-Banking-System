package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.Account;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface AccountService {

  // ======================================== GET Methods ========================================
  List<Account> getAll();

  Account getById(Long id) throws InstanceNotFoundException;

}
