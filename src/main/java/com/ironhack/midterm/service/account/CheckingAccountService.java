package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dto.AccountDTO;

import javax.management.InstanceNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface CheckingAccountService {

  // ======================================== GET ACCOUNT Methods ========================================
  List<CheckingAccount> getAll();


  // ======================================== ADD ACCOUNT Methods ========================================
  void newAccount(AccountDTO checkingAccount) throws InstanceNotFoundException, IllegalArgumentException, NoSuchAlgorithmException;


}
