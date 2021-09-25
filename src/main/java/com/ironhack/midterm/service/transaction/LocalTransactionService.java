package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dto.TransactionLocalDTO;

import javax.management.InstanceNotFoundException;

public interface LocalTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  Transaction newTransaction(long accountId, TransactionLocalDTO localTransaction) throws InstanceNotFoundException, IllegalArgumentException;

  void validateLocalTransaction(Transaction transaction) throws InstanceNotFoundException;

  void processTransaction(Transaction transaction) throws InstanceNotFoundException;


}
