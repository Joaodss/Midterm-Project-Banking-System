package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.LocalTransaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.LocalTransactionDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.LocalTransactionRepository;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.LocalTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.Currency;

import static com.ironhack.midterm.util.MoneyUtil.addMoney;
import static com.ironhack.midterm.util.MoneyUtil.subtractMoney;
import static com.ironhack.midterm.util.UserUtil.compareUserNames;

@Service
public class LocalTransactionServiceImpl implements LocalTransactionService {

  @Autowired
  private LocalTransactionRepository localTransactionRepository;

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerService accountManagerService;

  // ======================================== ADD TRANSACTION Methods ========================================
  public LocalTransaction newTransaction(long accountId, LocalTransactionDTO localTransaction) throws InstanceNotFoundException, IllegalArgumentException {
    Account ownerAccount = accountService.getById(accountId);
    Account targetAccount = accountService.getById(localTransaction.getTargetAccountId());
    AccountHolder targetOwner;

    if (compareUserNames(targetAccount.getPrimaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      targetOwner = targetAccount.getPrimaryOwner();
    } else if (targetAccount.getSecondaryOwner() != null && compareUserNames(targetAccount.getSecondaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      targetOwner = targetAccount.getSecondaryOwner();
    } else {
      throw new IllegalArgumentException("Target owner name does not correspond to target account");
    }

    return localTransactionRepository.save(
        new LocalTransaction(
            new Money(localTransaction.getTransferValue(), Currency.getInstance(localTransaction.getCurrency())),
            ownerAccount,
            targetAccount,
            targetOwner
        )
    );
  }

  public void validateLocalTransaction(LocalTransaction transaction) throws InstanceNotFoundException {
    if (accountManagerService.isTransactionTimeFraudulent(transaction.getBaseAccount(), transaction) ||
        accountManagerService.isTransactionDailyAmountFraudulent(transaction.getBaseAccount())) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceiverReceipt());
      transactionReceiptRepository.save(transaction.refuseAndGenerateSenderReceipt("Fraudulent behaviour detected! Base account was frozen."));
      accountService.freezeAccount(transaction.getBaseAccount().getId());

    } else if (accountManagerService.isTransactionAmountValid(transaction) &&
        accountManagerService.isAccountsNotFrozen(transaction)) {
      processTransaction(transaction);
      transactionReceiptRepository.save(transaction.acceptAndGenerateReceiverReceipt());
      transactionReceiptRepository.save(transaction.acceptAndGenerateSenderReceipt());

    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceiverReceipt());
      transactionReceiptRepository.save(transaction.refuseAndGenerateSenderReceipt("Account is frozen. Unable to complete the transaction."));

    } else if (!accountManagerService.isTransactionAmountValid(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceiverReceipt());
      transactionReceiptRepository.save(transaction.refuseAndGenerateSenderReceipt("Invalid amount to transfer."));
    }
    accountService.save(transaction.getBaseAccount());
    accountService.save(transaction.getTargetAccount());

  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(LocalTransaction transaction) throws InstanceNotFoundException {
    Account baseAccount = accountService.getById(transaction.getBaseAccount().getId());
    Account targetAccount = accountService.getById(transaction.getTargetAccount().getId());

    baseAccount.setBalance(subtractMoney(baseAccount.getBalance(), transaction.getConvertedAmount()));
    targetAccount.setBalance(addMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));
    accountService.save(baseAccount);
    accountService.save(targetAccount);

    accountManagerService.checkForAlterations(baseAccount);
    accountManagerService.checkForAlterations(targetAccount);
  }


}
