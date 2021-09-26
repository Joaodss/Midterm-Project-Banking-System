package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dto.TransactionThirdPartyDTO;

public interface ThirdPartyTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  void newTransaction(String hashedKey,TransactionThirdPartyDTO thirdPartyTransaction);

  void validateTransaction(Transaction transaction);

  void processTransaction(Transaction transaction);

  boolean isTransactionAmountValid(Transaction transaction);

}
