package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface TransactionService {

  // ======================================== GET Methods ========================================
  List<Transaction> getAllByAccountId(long AccountId) throws InstanceNotFoundException;

  Transaction getById(long transactionId) throws InstanceNotFoundException;

}
