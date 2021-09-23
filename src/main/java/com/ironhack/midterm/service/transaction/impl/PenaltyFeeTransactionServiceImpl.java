package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.PenaltyFeeTransaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.PenaltyFeeTransactionRepository;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.service.AccountManagerServiceImpl;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;

import static com.ironhack.midterm.util.MoneyUtil.subtractMoney;

@Service
public class PenaltyFeeTransactionServiceImpl implements PenaltyFeeTransactionService {

  @Autowired
  private PenaltyFeeTransactionRepository penaltyFeeTransactionRepository;

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerServiceImpl accountManagerService;

  // ======================================== ADD TRANSACTION Methods ========================================
  public PenaltyFeeTransaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException {
    Account account = accountService.getById(accountId);
    Money PenaltyFeeAmount = account.getPenaltyFee();
    return penaltyFeeTransactionRepository.save(new PenaltyFeeTransaction(PenaltyFeeAmount, account));
  }

  public PenaltyFeeTransaction newTransaction(long accountId, Money remaining) throws InstanceNotFoundException, IllegalArgumentException {
    Account account = accountService.getById(accountId);
    Money PenaltyFeeAmount = account.getPenaltyFee();
    return penaltyFeeTransactionRepository.save(new PenaltyFeeTransaction(PenaltyFeeAmount, account));
  }


  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validatePenaltyFeeTransaction(PenaltyFeeTransaction transaction) throws InstanceNotFoundException {
    if (accountManagerService.isTransactionAmountValid(transaction) && accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.acceptAndGenerateReceipt());
      processTransaction(transaction);
    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Account is frozen. Unable to withdraw penalty fee."));
    } else if (!accountManagerService.isTransactionAmountValid(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Insufficient founds to withdraw."));
      PenaltyFeeTransaction newTransaction = newTransaction(transaction.getTargetAccount().getId(), transaction.getTargetAccount().getBalance());
      validatePenaltyFeeTransaction(newTransaction);
      accountService.freezeAccount(transaction.getTargetAccount().getId());
    }
    accountService.save(transaction.getTargetAccount());
  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(PenaltyFeeTransaction transaction) throws InstanceNotFoundException {
    Account account = accountService.getById(transaction.getTargetAccount().getId());
    account.setBalance(subtractMoney(account.getBalance(), transaction.getConvertedAmount()));
    account.setLastPenaltyFee(account.getLastPenaltyFee().plusMonths(1));
    accountService.save(account);
    accountManagerService.checkForAlterations(account);
  }


}
