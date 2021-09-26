package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.MaintenanceFeeTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

import static com.ironhack.midterm.util.MoneyUtil.compareMoney;
import static com.ironhack.midterm.util.MoneyUtil.subtractMoney;

@Service
public class MaintenanceFeeTransactionServiceImpl implements MaintenanceFeeTransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerService accountManagerService;

  // ======================================== ADD TRANSACTION Methods ========================================
  public Transaction newTransaction(long accountId) throws EntityNotFoundException {
    Account account = accountService.getById(accountId);
    if (account.getClass() == CheckingAccount.class) {
      Money maintenanceFeeAmount = ((CheckingAccount) account).getMonthlyMaintenanceFee();
      return transactionRepository.save(new Transaction(maintenanceFeeAmount, account));
    }
    throw new IllegalArgumentException("Error when using account");
  }

  public Transaction newTransaction(long accountId, Money remaining) throws EntityNotFoundException {
    Account account = accountService.getById(accountId);
    if (account.getClass() == CheckingAccount.class) {
      return transactionRepository.save(new Transaction(remaining, account));
    }
    throw new IllegalArgumentException("Error when using account");
  }

  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validateMaintenanceFeeTransaction(Transaction transaction) throws EntityNotFoundException {
    if (isTransactionAmountValid(transaction) && accountManagerService.isAccountsNotFrozen(transaction)) {
      receiptRepository.save(transaction.generateMaintenanceFeeTransactionReceipt(true));
      processTransaction(transaction);
    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      receiptRepository.save(transaction.generateMaintenanceFeeTransactionReceipt(false, "Account is frozen. Unable to withdraw maintenance fee."));
    } else if (!isTransactionAmountValid(transaction)) {
      receiptRepository.save(transaction.generateMaintenanceFeeTransactionReceipt(false, "Insufficient founds to withdraw."));
      Transaction newTransaction = newTransaction(transaction.getTargetAccount().getId(), transaction.getTargetAccount().getBalance());
      validateMaintenanceFeeTransaction(newTransaction);
      accountService.freezeAccount(transaction.getTargetAccount().getId());
    }
    accountService.save(transaction.getTargetAccount());
  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) throws EntityNotFoundException {
    Account account = accountService.getById(transaction.getTargetAccount().getId());
    account.setBalance(subtractMoney(account.getBalance(), transaction.getConvertedAmount()));
    if (account.getClass() == CheckingAccount.class)
      ((CheckingAccount) account).setLastMaintenanceFee(((CheckingAccount) account).getLastMaintenanceFee().plusMonths(1));
    accountService.save(account);
    accountManagerService.checkForAlterations(account);
  }

  // (transfer money <= account balance and account not frozen)
  public boolean isTransactionAmountValid(Transaction transaction) {
    return compareMoney(transaction.getTargetAccount().getBalance(), transaction.getBaseAmount()) >= 0;
  }

}
