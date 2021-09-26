package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;

import javax.persistence.EntityNotFoundException;

public interface AccountManagerService {

  void checkForAlterations(Account account) throws EntityNotFoundException;

  boolean isAccountsNotFrozen(Transaction transaction);

  boolean isTransactionTimeFraudulent(Account account, Transaction transaction);

  boolean isTransactionDailyAmountFraudulent(Account account);


}
