package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

  // ======================================== GET Methods ========================================
  List<Transaction> getAllByAccountId(long AccountId) throws InstanceNotFoundException;

  List<Transaction> getByAccountIdByDateRange(long AccountId, Optional<String> startDate, Optional<String> endDate);

  Transaction getById(long transactionId) throws InstanceNotFoundException;

}
