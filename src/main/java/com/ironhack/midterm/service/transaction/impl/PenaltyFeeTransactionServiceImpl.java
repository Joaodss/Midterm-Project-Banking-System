package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;

import static com.ironhack.midterm.util.MoneyUtil.compareMoney;
import static com.ironhack.midterm.util.MoneyUtil.subtractMoney;

@Service
public class PenaltyFeeTransactionServiceImpl implements PenaltyFeeTransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerService accountManagerService;

  // ======================================== ADD TRANSACTION Methods ========================================
  public Transaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException {
    Account account = accountService.getById(accountId);
    Money PenaltyFeeAmount = account.getPenaltyFee();
    return transactionRepository.save(new Transaction(PenaltyFeeAmount, account));
  }

  public Transaction newTransaction(long accountId, Money remaining) throws InstanceNotFoundException, IllegalArgumentException {
    Account account = accountService.getById(accountId);
    Money PenaltyFeeAmount = account.getPenaltyFee();
    return transactionRepository.save(new Transaction(PenaltyFeeAmount, account));
  }


  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validatePenaltyFeeTransaction(Transaction transaction) throws InstanceNotFoundException {
    if (isTransactionAmountValid(transaction) && accountManagerService.isAccountsNotFrozen(transaction)) {
      receiptRepository.save(transaction.generatePenaltyFeeTransactionReceipt(true));
      processTransaction(transaction);
    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      receiptRepository.save(transaction.generatePenaltyFeeTransactionReceipt(false, "Account is frozen. Unable to withdraw penalty fee."));
    } else if (!isTransactionAmountValid(transaction)) {
      receiptRepository.save(transaction.generatePenaltyFeeTransactionReceipt(false, "Insufficient founds to withdraw."));
      Transaction newTransaction = newTransaction(transaction.getTargetAccount().getId(), transaction.getTargetAccount().getBalance());
      validatePenaltyFeeTransaction(newTransaction);
      accountService.freezeAccount(transaction.getTargetAccount().getId());
    }
    accountService.save(transaction.getTargetAccount());
  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) throws InstanceNotFoundException {
    Account account = accountService.getById(transaction.getTargetAccount().getId());
    account.setBalance(subtractMoney(account.getBalance(), transaction.getConvertedAmount()));
    account.setLastPenaltyFeeCheck(account.getLastPenaltyFeeCheck().plusMonths(1));
    accountService.save(account);
    accountManagerService.checkForAlterations(account);
  }

  // (transfer money <= account balance and account not frozen)
  public boolean isTransactionAmountValid(Transaction transaction) {
    return compareMoney(transaction.getTargetAccount().getBalance(), transaction.getBaseAmount()) >= 0;
  }


}
