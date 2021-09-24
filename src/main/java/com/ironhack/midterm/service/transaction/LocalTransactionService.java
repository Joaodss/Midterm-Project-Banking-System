package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.LocalTransaction;
import com.ironhack.midterm.dto.TransactionLocalDTO;

import javax.management.InstanceNotFoundException;

public interface LocalTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  LocalTransaction newTransaction(long accountId, TransactionLocalDTO localTransaction) throws InstanceNotFoundException, IllegalArgumentException;

  void validateLocalTransaction(LocalTransaction transaction) throws InstanceNotFoundException;

  void processTransaction(LocalTransaction transaction) throws InstanceNotFoundException;


}
