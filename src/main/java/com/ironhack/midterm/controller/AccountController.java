package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dto.CreditCardDTO;

import java.util.List;

public interface AccountController {

  // ======================================== GET Methods ========================================
  // -------------------- All Users [ADMIN] --------------------
  List<Account> getAccounts();

//  List<CheckingAccount> getCheckingAccounts();
//
//  List<StudentCheckingAccount> getStudentCheckingAccounts();
//
//  List<SavingsAccount> getSavingsAccounts();

  List<CreditCard> getCreditCards();

  Account getAccountById(long id);


  // ======================================== POST Methods ========================================
//  // -------------------- New Checking Account [ADMIN] --------------------
//  void createCheckingAccount(CheckingAccountDTO checkingAccount);
//
//  // -------------------- New Savings Account [ADMIN] --------------------
//  void createSavingsAccount(SavingsAccountDTO savingsAccount);

  // -------------------- New Credit Card [ADMIN] --------------------
  void createCreditCard(CreditCardDTO creditCard);


  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
