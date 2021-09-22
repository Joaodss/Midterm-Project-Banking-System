package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import com.ironhack.midterm.dto.LocalTransactionDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TransactionController {

  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  List<Transaction> getTransactions(Authentication auth, long id);

  // -------------------- User Specific Transactions by Date Range [ADMIN / Specific USER] --------------------
//  List<Transaction> getTransactionsByDateRange(Authentication auth, long id, Optional<String> startDate, Optional<String> endDate);

  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  Transaction getTransactionsById(Authentication auth, long accountId, long transactionId);


  // ======================================== GET RECEIPT Methods ========================================
  // -------------------- User Specific Receipt by Id [ADMIN / Specific USER] --------------------
  TransactionReceipt getReceiptsByTransactionId(Authentication auth, long accountId, long transactionId);


  // ======================================== POST TRANSACTION Methods ========================================
  // -------------------- Add Account Specific Local Transaction [ADMIN / Specific USER] --------------------
  void createLocalTransaction(Authentication auth, long id, LocalTransactionDTO localTransaction);

  // -------------------- Add Account Specific Third Party Transaction [ADMIN / Specific USER] --------------------
//  void createThirdPartyTransaction(ThirdPartyTransactionDTO localTransaction);


}
