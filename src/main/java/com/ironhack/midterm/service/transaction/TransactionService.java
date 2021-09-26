package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;

import java.util.List;

public interface TransactionService {

  // ======================================== get Methods ========================================
  List<Transaction> getAllByAccountId(long AccountId);

  Transaction getById(long AccountId, long transactionId);

}
