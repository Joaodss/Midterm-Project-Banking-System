package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.model.Money;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AccountController {

  // ======================================== GET Methods ========================================
  // -------------------- All Users [ADMIN] --------------------
  List<Account> getAccounts(Authentication auth);

  Account getAccountById(Authentication auth, long id);

  Money getAccountBalanceById(Authentication auth, long id);

  List<CheckingAccount> getCheckingAccounts();

  List<StudentCheckingAccount> getStudentCheckingAccounts();

  List<SavingsAccount> getSavingsAccounts();

  List<CreditCard> getCreditCards();


  // ======================================== POST Methods ========================================
  // -------------------- New Checking Account [ADMIN] --------------------
  void createCheckingAccount(AccountDTO checkingAccount);

  // -------------------- New Savings Account [ADMIN] --------------------
  void createSavingsAccount(AccountDTO savingsAccount);

  // -------------------- New Credit Card [ADMIN] --------------------
  void createCreditCard(AccountDTO creditCard);


  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
