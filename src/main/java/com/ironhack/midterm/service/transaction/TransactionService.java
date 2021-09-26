package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface TransactionService {

  // ======================================== get Methods ========================================
  List<Transaction> getAllByAccountId(long AccountId);

  Transaction getById(long AccountId, long transactionId);

  // ======================================== utils Methods ========================================
  boolean isAccountFrozen(Transaction transaction);

  // ======================================== fraud detection Methods ========================================
  boolean isTransactionTimeFraudulent(Account account);

  boolean isTransactionDailyAmountFraudulent(Account account);

  // ======================================== utils of fraud detection ========================================
  Money lastDayTransactions(Account account);

  Money allDailyMax(Account account);

  HashMap<LocalDate, Money> dailyTransactions(Account account);

}
