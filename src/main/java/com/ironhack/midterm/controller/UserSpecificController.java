package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.request.Request;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import com.ironhack.midterm.model.Money;

import java.util.List;

public interface UserSpecificController {

  // ======================================== GET Methods ========================================
  // -------------------- Username Specific Accounts [USER username, ADMIN]--------------------
  List<Account> getUserAccounts(String username);

  List<Account> getUserAccountById(String username, long accountId);

  Money getUserAccountBalanceById(String username, long accountId);


  // -------------------- Username Specific Accounts Transactions [USER username, ADMIN]--------------------
  List<Transaction> getUserAccountTransactions(String username, long accountId);

  List<TransactionReceipt> getUserAccountTransactionsByType(String username, long accountId, String transactionType, String status);

  Transaction getUserAccountTransactionById(String username, long accountId, long transactionId);


  // -------------------- Username Specific Accounts Transactions Receipts [USER username, ADMIN]--------------------
  List<TransactionReceipt> getUserAccountReceipts(String username, long accountId);

  List<TransactionReceipt> getUserAccountReceiptsByType(String username, long accountId, String transactionType, String status);

  TransactionReceipt getUserAccountReceiptById(String username, long accountId, long transactionId);


  // -------------------- Username Specific Requests [USER username, ADMIN]--------------------
  List<Request> getUserRequests(String username);

  List<Request> getUserRequestsByType(String username, String Status, String requestType);

  Request getUserRequestById(String username, long requestId);


  // ======================================== POST Methods ========================================

  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
