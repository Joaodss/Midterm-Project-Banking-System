package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;

import javax.management.InstanceNotFoundException;

public interface AccountManagerService {

  void checkForAlterations(Account account) throws InstanceNotFoundException;

  boolean isTransactionAmountValid(Transaction transaction);

  boolean isAccountsNotFrozen(Transaction transaction);

  boolean isTransactionTimeFraudulent(Account account, Transaction transaction);

  boolean isTransactionDailyAmountFraudulent(Account account);


}
