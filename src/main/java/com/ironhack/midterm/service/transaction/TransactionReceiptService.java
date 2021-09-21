package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.TransactionReceipt;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

public interface TransactionReceiptService {

  // ======================================== GET Methods ========================================
  List<TransactionReceipt> getAllByAccountId(long AccountId) throws InstanceNotFoundException;

  List<TransactionReceipt> getByAccountIdByDateRange(long AccountId, Optional<String> startDate, Optional<String> endDate);

  TransactionReceipt getById(long transactionReceiptId) throws InstanceNotFoundException;

}
