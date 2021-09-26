package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;

import javax.persistence.EntityNotFoundException;

public interface InterestTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  Transaction newTransaction(long accountId) throws EntityNotFoundException, IllegalArgumentException;

  void validateInterestTransaction(Transaction transaction) throws EntityNotFoundException;

  void processTransaction(Transaction transaction) throws EntityNotFoundException;


}
