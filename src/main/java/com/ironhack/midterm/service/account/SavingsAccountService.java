package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dto.AccountDTO;

import javax.management.InstanceNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface SavingsAccountService {

  // ======================================== GET ACCOUNT Methods ========================================
  List<SavingsAccount> getAll();


  // ======================================== ADD ACCOUNT Methods ========================================
  void newUser(AccountDTO savingsAccount) throws InstanceNotFoundException, IllegalArgumentException, NoSuchAlgorithmException;


}
