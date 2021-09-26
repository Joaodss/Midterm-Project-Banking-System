package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dto.AccountDTO;

import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface CheckingAccountService {

  // ======================================== get Methods ========================================
  List<CheckingAccount> getAll();

  // ======================================== new Methods ========================================
  void newAccount(AccountDTO checkingAccount) throws EntityNotFoundException, IllegalArgumentException, NoSuchAlgorithmException;

  // ======================================== update balance Methods ========================================
  void checkMaintenanceFee(CheckingAccount checkingAccount);

  void checkMinimumBalance(CheckingAccount checkingAccount);

}
