package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.PenaltyFeeTransaction;
import com.ironhack.midterm.model.Money;

import javax.management.InstanceNotFoundException;

public interface PenaltyFeeTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  PenaltyFeeTransaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException;

  PenaltyFeeTransaction newTransaction(long accountId, Money remaining) throws InstanceNotFoundException, IllegalArgumentException;


  void validatePenaltyFeeTransaction(PenaltyFeeTransaction transaction) throws InstanceNotFoundException;

  void processTransaction(PenaltyFeeTransaction transaction) throws InstanceNotFoundException;

}
