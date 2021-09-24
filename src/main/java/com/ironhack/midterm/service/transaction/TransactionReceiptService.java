package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Receipt;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

public interface TransactionReceiptService {

  // ======================================== GET Methods ========================================
  List<Receipt> getAllByAccountId(long AccountId) throws InstanceNotFoundException;

  List<Receipt> getByAccountIdByDateRange(long AccountId, Optional<String> startDate, Optional<String> endDate);

  Receipt getById(long transactionReceiptId) throws InstanceNotFoundException;

}
