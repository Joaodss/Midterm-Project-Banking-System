package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dto.LocalTransactionDTO;

import javax.management.InstanceNotFoundException;

public interface LocalTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  void newTransaction(long accountId, LocalTransactionDTO localTransaction) throws InstanceNotFoundException, IllegalArgumentException;

}
