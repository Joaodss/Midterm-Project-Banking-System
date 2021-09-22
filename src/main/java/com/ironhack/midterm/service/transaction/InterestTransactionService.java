package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.InterestTransaction;

import javax.management.InstanceNotFoundException;

public interface InterestTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  InterestTransaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException;

  void validateInterestTransaction(InterestTransaction transaction) throws InstanceNotFoundException;

  void processTransaction(InterestTransaction transaction) throws InstanceNotFoundException;


}
