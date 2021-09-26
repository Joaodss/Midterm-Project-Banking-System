package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.TransactionLocalDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.LocalTransactionService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Currency;

import static com.ironhack.midterm.util.MoneyUtil.*;
import static com.ironhack.midterm.util.UserUtil.isSameUserName;

@Service
public class LocalTransactionServiceImpl implements LocalTransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransactionService transactionService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public void newTransaction(long accountId, TransactionLocalDTO localTransaction) {
    Account ownerAccount = accountService.getById(accountId);
    Account targetAccount = accountService.getById(localTransaction.getTargetAccountId());

    AccountHolder targetOwner;
    if (isSameUserName(targetAccount.getPrimaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      targetOwner = targetAccount.getPrimaryOwner();
    } else if (targetAccount.getSecondaryOwner() != null && isSameUserName(targetAccount.getSecondaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      targetOwner = targetAccount.getSecondaryOwner();
    } else throw new IllegalArgumentException("Target owner name does not correspond to target account owner.");


    Transaction transaction = transactionRepository.save(
        new Transaction(
            new Money(localTransaction.getTransferValue(), Currency.getInstance(localTransaction.getCurrency())),
            ownerAccount,
            targetAccount,
            targetOwner)
    );
    validateTransaction(transaction);
    accountService.updateBalance(targetAccount);
    accountService.updateBalance(ownerAccount);
  }

  public void validateTransaction(Transaction transaction) {
    // Check if frozen.
    if (transactionService.isAccountFrozen(transaction)) {
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(false, "Unable to complete the transaction. One of the accounts involved in the transaction is unavailable (frozen)."));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(false, "Unable to complete the transaction. One of the accounts involved in the transaction is unavailable (frozen)."));

      // Check if fraudulent.
    } else if (transactionService.isTransactionTimeFraudulent(transaction.getBaseAccount()) ||
        transactionService.isTransactionDailyAmountFraudulent(transaction.getBaseAccount())) {
      accountService.freezeAccount(transaction.getBaseAccount().getId());
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(false));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(false, "Fraudulent behaviour detected! Base account was frozen."));

      // Check if transaction amount is not valid.
    } else if (!isTransactionAmountValid(transaction)) {
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(false));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(false, "Invalid amount to transfer."));

      // If there are no constrains, accept and process transaction.
    } else {
      processTransaction(transaction);
      receiptRepository.save(transaction.generateLocalTransactionReceiverReceipt(true));
      receiptRepository.save(transaction.generateLocalTransactionSenderReceipt(true));
    }
    accountService.save(transaction.getBaseAccount());
    accountService.save(transaction.getTargetAccount());
  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) {
    Account baseAccount = accountService.getById(transaction.getBaseAccount().getId());
    Account targetAccount = accountService.getById(transaction.getTargetAccount().getId());

    baseAccount.setBalance(subtractMoney(baseAccount.getBalance(), transaction.getConvertedAmount()));
    targetAccount.setBalance(addMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));

    accountService.save(baseAccount);
    accountService.save(targetAccount);
  }


  // ======================================== util Methods ========================================
  // (transfer money <= account balance)
  public boolean isTransactionAmountValid(Transaction transaction) {
    return compareMoney(transaction.getBaseAccount().getBalance(), transaction.getBaseAmount()) >= 0;
  }

}
