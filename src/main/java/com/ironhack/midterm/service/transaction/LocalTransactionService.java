package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dto.TransactionLocalDTO;

public interface LocalTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  void newTransaction(long accountId, TransactionLocalDTO localTransaction);

  void validateTransaction(Transaction transaction);

  void processTransaction(Transaction transaction);

  boolean isTransactionAmountValid(Transaction transaction);


}
