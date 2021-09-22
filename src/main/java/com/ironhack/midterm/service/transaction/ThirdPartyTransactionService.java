package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dto.ThirdPartyTransactionDTO;

import javax.management.InstanceNotFoundException;

public interface ThirdPartyTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  void newTransaction(ThirdPartyTransactionDTO thirdPartyTransaction) throws InstanceNotFoundException, IllegalArgumentException;

}
