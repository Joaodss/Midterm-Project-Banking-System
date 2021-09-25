package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.TransactionLocalDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.LocalTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.Currency;

import static com.ironhack.midterm.util.MoneyUtil.*;
import static com.ironhack.midterm.util.UserUtil.compareUserNames;

@Service
public class LocalTransactionServiceImpl implements LocalTransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerService accountManagerService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public Transaction newTransaction(long accountId, TransactionLocalDTO localTransaction) throws InstanceNotFoundException, IllegalArgumentException {
    Account ownerAccount = accountService.getById(accountId);
    Account targetAccount = accountService.getById(localTransaction.getTargetAccountId());
    AccountHolder targetOwner;

    if (compareUserNames(targetAccount.getPrimaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      targetOwner = targetAccount.getPrimaryOwner();
    } else if (targetAccount.getSecondaryOwner() != null && compareUserNames(targetAccount.getSecondaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      targetOwner = targetAccount.getSecondaryOwner();
    } else {
      throw new IllegalArgumentException("Target owner name does not correspond to target account owner.");
    }

    return transactionRepository.save(
        new Transaction(
            new Money(localTransaction.getTransferValue(), Currency.getInstance(localTransaction.getCurrency())),
            ownerAccount,
            targetAccount,
            targetOwner
        )
    );
  }

  public void validateLocalTransaction(Transaction transaction) throws InstanceNotFoundException {
    if (accountManagerService.isTransactionTimeFraudulent(transaction.getBaseAccount(), transaction) ||
        accountManagerService.isTransactionDailyAmountFraudulent(transaction.getBaseAccount())) {
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(false));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(false, "Fraudulent behaviour detected! Base account was frozen."));
      accountService.freezeAccount(transaction.getBaseAccount().getId());

    } else if (isTransactionAmountValid(transaction) &&
        accountManagerService.isAccountsNotFrozen(transaction)) {
      processTransaction(transaction);
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(true));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(true));

    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(false));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(false, "Account is frozen. Unable to complete the transaction."));

    } else if (!isTransactionAmountValid(transaction)) {
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(false));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(false, "Invalid amount to transfer."));
    }
    accountService.save(transaction.getBaseAccount());
    accountService.save(transaction.getTargetAccount());

  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) throws InstanceNotFoundException {
    Account baseAccount = accountService.getById(transaction.getBaseAccount().getId());
    Account targetAccount = accountService.getById(transaction.getTargetAccount().getId());

    baseAccount.setBalance(subtractMoney(baseAccount.getBalance(), transaction.getConvertedAmount()));
    targetAccount.setBalance(addMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));
    accountService.save(baseAccount);
    accountService.save(targetAccount);

    accountManagerService.checkForAlterations(baseAccount);
    accountManagerService.checkForAlterations(targetAccount);
  }

  // (transfer money <= account balance)
  public boolean isTransactionAmountValid(Transaction transaction) {
    return compareMoney(transaction.getBaseAccount().getBalance(), transaction.getBaseAmount()) >= 0;
  }


}
