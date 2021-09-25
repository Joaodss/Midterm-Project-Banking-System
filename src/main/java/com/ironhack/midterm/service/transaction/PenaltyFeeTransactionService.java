package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;

import javax.management.InstanceNotFoundException;

public interface PenaltyFeeTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  Transaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException;

  Transaction newTransaction(long accountId, Money remaining) throws InstanceNotFoundException, IllegalArgumentException;


  void validatePenaltyFeeTransaction(Transaction transaction) throws InstanceNotFoundException;

  void processTransaction(Transaction transaction) throws InstanceNotFoundException;

}
