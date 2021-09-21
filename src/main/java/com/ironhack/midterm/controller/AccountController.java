package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.model.Money;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AccountController {

  // ======================================== GET ACCOUNT Methods ========================================
  // -------------------- All Accounts [ADMIN] / User Specific Accounts [Specific USER] --------------------
  List<Account> getAccounts(Authentication auth);

  // -------------------- Account by Id [ADMIN] / User Specific Account by Id [Specific USER] --------------------
  Account getAccountById(Authentication auth, long id);

  // -------------------- Account Balance by Id [ADMIN] / User Specific Account Balance by Id [Specific USER] --------------------
  Money getAccountBalanceById(Authentication auth, long id);

  // -------------------- All Checking Accounts [ADMIN] --------------------
  List<CheckingAccount> getCheckingAccounts();

  // -------------------- All Student Checking Accounts [ADMIN] --------------------
  List<StudentCheckingAccount> getStudentCheckingAccounts();

  // -------------------- All Savings Accounts [ADMIN] --------------------
  List<SavingsAccount> getSavingsAccounts();

  // -------------------- All Credit Cards [ADMIN] --------------------
  List<CreditCard> getCreditCards();

  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  List<Transaction> getTransactions(Authentication auth, long id);

  // -------------------- User Specific Transactions by Date Range [ADMIN / Specific USER] --------------------
//  List<Transaction> getTransactionsByDateRange(Authentication auth, long id, Optional<String> startDate, Optional<String> endDate);

  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  Transaction getTransactionsById(Authentication auth, long transactionId);


  // ======================================== GET RECEIPT Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  List<TransactionReceipt> getReceipts(Authentication auth, long id);

  // -------------------- User Specific Transactions by Date Range [ADMIN / Specific USER] --------------------
//  List<TransactionReceipt> getReceiptsByTransactionsDateRange(Authentication auth, long id, Optional<String> startDate, Optional<String> endDate);

  // -------------------- User Specific Receipt by Id [ADMIN / Specific USER] --------------------
  TransactionReceipt getReceiptsByTransactionId(Authentication auth, long receiptId);


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
