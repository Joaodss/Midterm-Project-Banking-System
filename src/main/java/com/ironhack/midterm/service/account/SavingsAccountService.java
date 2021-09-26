package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dto.AccountDTO;

import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface SavingsAccountService {

  // ======================================== get Methods ========================================
  List<SavingsAccount> getAll();

  // ======================================== new Methods ========================================
  void newAccount(AccountDTO savingsAccount) throws EntityNotFoundException, IllegalArgumentException, NoSuchAlgorithmException;

  // ======================================== update balance Methods ========================================
  void checkInterestRate(SavingsAccount savingsAccount);

  void checkMinimumBalance(SavingsAccount savingsAccount);

}
