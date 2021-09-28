package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dto.TransactionThirdPartyDTO;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.ThirdPartyTransactionService;
import com.ironhack.midterm.service.transaction.TransactionService;
import com.ironhack.midterm.service.user.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Currency;

import static com.ironhack.midterm.util.EnumsUtil.transactionPurposeFromString;
import static com.ironhack.midterm.util.MoneyUtil.*;

@Service
public class ThirdPartyTransactionServiceImpl implements ThirdPartyTransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private ThirdPartyService thirdPartyService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public void newTransaction(String hashedKey, TransactionThirdPartyDTO thirdPartyTransaction) {
    if (!thirdPartyService.hasHashedKey(hashedKey)) throw new IllegalArgumentException("Invalid hashed key.");

    Account targetAccount = accountService.getById(thirdPartyTransaction.getTargetAccountId());
    boolean isValidKey;
    if (targetAccount.getClass() == CheckingAccount.class) {
      isValidKey = (((CheckingAccount) targetAccount).getSecretKey().equals(thirdPartyTransaction.getSecretKey()));
    } else if (targetAccount.getClass() == StudentCheckingAccount.class) {
      isValidKey = (((StudentCheckingAccount) targetAccount).getSecretKey().equals(thirdPartyTransaction.getSecretKey()));
    } else if (targetAccount.getClass() == SavingsAccount.class) {
      isValidKey = (((SavingsAccount) targetAccount).getSecretKey().equals(thirdPartyTransaction.getSecretKey()));
    } else {
      throw new IllegalArgumentException("Account not valid for third party transactions.");
    }
    if (!isValidKey) throw new IllegalArgumentException("Account key is not valid for the targeted account.");

    Transaction transaction = transactionRepository.save(
        new Transaction(
            new Money(thirdPartyTransaction.getTransferValue(), Currency.getInstance(thirdPartyTransaction.getCurrency())),
            targetAccount,
            transactionPurposeFromString(thirdPartyTransaction.getTransactionPurpose())
        )
    );
    validateTransaction(transaction);
    accountService.updateBalance(targetAccount);
  }

  public void validateTransaction(Transaction transaction) {
    // Check if frozen.
    if (transactionService.isAccountFrozen(transaction)) {
      receiptRepository.save(transaction.generateThirdPartyTransactionReceipt(false, "Account is frozen. Unable to complete the transaction."));

      // Check if fraudulent.
    } else if (transaction.getTransactionPurpose() == TransactionPurpose.REQUEST &&
        (transactionService.isTransactionTimeFraudulent(transaction.getTargetAccount()) ||
            transactionService.isTransactionDailyAmountFraudulent(transaction.getTargetAccount()))) {
      accountService.freezeAccount(transaction.getTargetAccount().getId());
      receiptRepository.save(transaction.generateThirdPartyTransactionReceipt(false, "Fraudulent behaviour detected! Base account was frozen for safety."));

      // Check if transaction amount is not valid.
    } else if (!isTransactionAmountValid(transaction)) {
      receiptRepository.save(transaction.generateThirdPartyTransactionReceipt(false, "Invalid amount to transfer."));

      // If there are no constrains, accept and process transaction.
    } else {
      processTransaction(transaction);
      receiptRepository.save(transaction.generateThirdPartyTransactionReceipt(true));
    }
    accountService.save(transaction.getTargetAccount());
  }

  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) {
    Account targetAccount = accountService.getById(transaction.getTargetAccount().getId());

    if (transaction.getTransactionPurpose() == TransactionPurpose.REQUEST) {
      targetAccount.setBalance(subtractMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));
    } else {
      targetAccount.setBalance(addMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));
    }
    accountService.save(targetAccount);
  }


  // (transfer money <= account balance)
  public boolean isTransactionAmountValid(Transaction transaction) {
    if (transaction.getTransactionPurpose() != null && transaction.getTransactionPurpose() == TransactionPurpose.REQUEST) {
      return compareMoney(transaction.getTargetAccount().getBalance(), transaction.getBaseAmount()) >= 0;
    } else return transaction.getTransactionPurpose() != null && transaction.getTransactionPurpose() == TransactionPurpose.SEND;
  }

}
