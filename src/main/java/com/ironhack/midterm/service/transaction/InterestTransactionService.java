package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;

import javax.management.InstanceNotFoundException;

public interface InterestTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  Transaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException;

  void validateInterestTransaction(Transaction transaction) throws InstanceNotFoundException;

  void processTransaction(Transaction transaction) throws InstanceNotFoundException;


}
