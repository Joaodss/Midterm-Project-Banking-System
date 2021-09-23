package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.transaction.MaintenanceFeeTransaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.MaintenanceFeeTransactionRepository;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.service.AccountManagerServiceImpl;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.MaintenanceFeeTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;

import static com.ironhack.midterm.util.MoneyUtil.subtractMoney;

@Service
public class MaintenanceFeeTransactionServiceImpl implements MaintenanceFeeTransactionService {

  @Autowired
  private MaintenanceFeeTransactionRepository maintenanceFeeTransactionRepository;

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerServiceImpl accountManagerService;

  // ======================================== ADD TRANSACTION Methods ========================================
  public MaintenanceFeeTransaction newTransaction(long accountId) throws InstanceNotFoundException {
    Account account = accountService.getById(accountId);
    if (account.getClass() == CheckingAccount.class) {
      Money maintenanceFeeAmount = ((CheckingAccount) account).getMonthlyMaintenanceFee();
      return maintenanceFeeTransactionRepository.save(new MaintenanceFeeTransaction(maintenanceFeeAmount, account));
    }
    throw new IllegalArgumentException("Error when using account");
  }

  public MaintenanceFeeTransaction newTransaction(long accountId, Money remaining) throws InstanceNotFoundException {
    Account account = accountService.getById(accountId);
    if (account.getClass() == CheckingAccount.class) {
      return maintenanceFeeTransactionRepository.save(new MaintenanceFeeTransaction(remaining, account));
    }
    throw new IllegalArgumentException("Error when using account");
  }

  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validateMaintenanceFeeTransaction(MaintenanceFeeTransaction transaction) throws InstanceNotFoundException {
    if (accountManagerService.isTransactionAmountValid(transaction) && accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.acceptAndGenerateReceipt());
      processTransaction(transaction);
    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Account is frozen. Unable to withdraw maintenance fee."));
    } else if (!accountManagerService.isTransactionAmountValid(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Insufficient founds to withdraw."));
      MaintenanceFeeTransaction newTransaction = newTransaction(transaction.getTargetAccount().getId(), transaction.getTargetAccount().getBalance());
      validateMaintenanceFeeTransaction(newTransaction);
      accountService.freezeAccount(transaction.getTargetAccount().getId());
    }
    accountService.save(transaction.getTargetAccount());
  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(MaintenanceFeeTransaction transaction) throws InstanceNotFoundException {
    Account account = accountService.getById(transaction.getTargetAccount().getId());
    account.setBalance(subtractMoney(account.getBalance(), transaction.getConvertedAmount()));
    if (account.getClass() == CheckingAccount.class)
      ((CheckingAccount) account).setLastMaintenanceFee(((CheckingAccount) account).getLastMaintenanceFee().plusMonths(1));
    accountService.save(account);
    accountManagerService.checkForAlterations(account);
  }

}
