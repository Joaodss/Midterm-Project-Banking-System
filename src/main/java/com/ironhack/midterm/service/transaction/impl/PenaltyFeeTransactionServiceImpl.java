package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  private TransactionService transactionService;

  // ======================================== ADD TRANSACTION Methods ========================================
  public void newTransaction(long accountId) {
    Account account = accountService.getById(accountId);

    Money PenaltyFeeAmount = account.getPenaltyFee();

    Transaction transaction = transactionRepository.save(
        new Transaction(PenaltyFeeAmount, account)
    );
    validateTransaction(transaction);
    accountService.updateBalance(account);
  }

  public void newTransaction(long accountId, Money remaining) {
    Account account = accountService.getById(accountId);

    Transaction transaction = transactionRepository.save(
        new Transaction(remaining, account)
    );
    processTransaction(transaction);
    receiptRepository.save(transaction.generateMaintenanceFeeTransactionReceipt(true));
  }


  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validateTransaction(Transaction transaction) {
    // Check if frozen.
    if (transactionService.isAccountFrozen(transaction)) {
      receiptRepository.save(transaction.generatePenaltyFeeTransactionReceipt(false, "Account is frozen. Unable to withdraw penalty fee."));

      // Check if transaction amount is not valid.
    } else if (!isTransactionAmountValid(transaction)) {
      accountService.freezeAccount(transaction.getTargetAccount().getId());
      receiptRepository.save(transaction.generatePenaltyFeeTransactionReceipt(false, "Insufficient founds to withdraw."));
      newTransaction(transaction.getTargetAccount().getId(), transaction.getTargetAccount().getBalance());

      // If there are no constrains, accept and process transaction.
    } else {
      processTransaction(transaction);
      receiptRepository.save(transaction.generatePenaltyFeeTransactionReceipt(true));
    }
    accountService.save(transaction.getTargetAccount());
  }


  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) {
    Account account = accountService.getById(transaction.getTargetAccount().getId());

    account.setBalance(subtractMoney(account.getBalance(), transaction.getConvertedAmount()));
    account.setLastPenaltyFeeCheck(account.getLastPenaltyFeeCheck().plusMonths(1));

    accountService.save(account);
  }

  // (transfer money <= account balance and account not frozen)
  public boolean isTransactionAmountValid(Transaction transaction) {
    return compareMoney(transaction.getTargetAccount().getBalance(), transaction.getBaseAmount()) >= 0;
  }

}
