package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;

public interface InterestTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  void newTransaction(long accountId);

  void validateTransaction(Transaction transaction);

  void processTransaction(Transaction transaction);

}
