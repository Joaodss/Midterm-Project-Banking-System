package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;

public interface MaintenanceFeeTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  void newTransaction(long accountId);

  void newTransaction(long accountId, Money remaining);


  void validateTransaction(Transaction transaction);

  void processTransaction(Transaction transaction);

  boolean isTransactionAmountValid(Transaction transaction);

}
