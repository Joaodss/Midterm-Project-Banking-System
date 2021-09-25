package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dto.TransactionThirdPartyDTO;

import javax.management.InstanceNotFoundException;

public interface ThirdPartyTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  Transaction newTransaction(TransactionThirdPartyDTO thirdPartyTransaction) throws InstanceNotFoundException, IllegalArgumentException;

  void validateThirdPartyTransaction(Transaction transaction) throws InstanceNotFoundException;

  void processTransaction(Transaction transaction) throws InstanceNotFoundException;

}
