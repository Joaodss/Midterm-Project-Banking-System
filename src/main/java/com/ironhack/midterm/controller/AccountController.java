package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dto.AccountDTO;

import java.util.List;

public interface AccountController {

  // ======================================== GET Methods ========================================
  // -------------------- All Users [ADMIN] --------------------
  List<Account> getAccounts();

  List<CheckingAccount> getCheckingAccounts();

  List<StudentCheckingAccount> getStudentCheckingAccounts();

  List<SavingsAccount> getSavingsAccounts();

  List<CreditCard> getCreditCards();

  Account getAccountById(long id);


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
