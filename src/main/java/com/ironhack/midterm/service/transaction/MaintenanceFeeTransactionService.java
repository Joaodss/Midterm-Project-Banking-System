package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.MaintenanceFeeTransaction;
import com.ironhack.midterm.model.Money;

import javax.management.InstanceNotFoundException;

public interface MaintenanceFeeTransactionService {

  // ======================================== ADD TRANSACTION Methods ========================================
  MaintenanceFeeTransaction newTransaction(long accountId) throws InstanceNotFoundException;

  MaintenanceFeeTransaction newTransaction(long accountId, Money remaining) throws InstanceNotFoundException;


  void validateMaintenanceFeeTransaction(MaintenanceFeeTransaction transaction) throws InstanceNotFoundException;

  void processTransaction(MaintenanceFeeTransaction transaction) throws InstanceNotFoundException;

}
