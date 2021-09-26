package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;

import javax.persistence.EntityNotFoundException;

public interface MaintenanceFeeTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  Transaction newTransaction(long accountId) throws EntityNotFoundException;

  Transaction newTransaction(long accountId, Money remaining) throws EntityNotFoundException;


  void validateMaintenanceFeeTransaction(Transaction transaction) throws EntityNotFoundException;

  void processTransaction(Transaction transaction) throws EntityNotFoundException;

}
