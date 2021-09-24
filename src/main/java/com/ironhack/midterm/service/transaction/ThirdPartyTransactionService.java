package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.LocalTransaction;
import com.ironhack.midterm.dao.transaction.ThirdPartyTransaction;
import com.ironhack.midterm.dto.ThirdPartyTransactionDTO;

import javax.management.InstanceNotFoundException;

public interface ThirdPartyTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  ThirdPartyTransaction newTransaction(ThirdPartyTransactionDTO thirdPartyTransaction) throws InstanceNotFoundException, IllegalArgumentException;

  void validateThirdPartyTransaction(ThirdPartyTransaction transaction) throws InstanceNotFoundException;

  void processTransaction(ThirdPartyTransaction transaction) throws InstanceNotFoundException;

}
