package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.ThirdPartyTransaction;
import com.ironhack.midterm.dto.ThirdPartyTransactionDTO;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ThirdPartyTransactionRepository;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.ThirdPartyTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.Currency;

import static com.ironhack.midterm.util.EnumsUtil.transactionPurposeFromString;
import static com.ironhack.midterm.util.MoneyUtil.addMoney;
import static com.ironhack.midterm.util.MoneyUtil.subtractMoney;

@Service
public class ThirdPartyTransactionServiceImpl implements ThirdPartyTransactionService {

  @Autowired
  private ThirdPartyTransactionRepository thirdPartyTransactionRepository;

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerService accountManagerService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public ThirdPartyTransaction newTransaction(ThirdPartyTransactionDTO thirdPartyTransaction) throws InstanceNotFoundException, IllegalArgumentException {
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

    return thirdPartyTransactionRepository.save(
        new ThirdPartyTransaction(
            new Money(thirdPartyTransaction.getTransferValue(), Currency.getInstance(thirdPartyTransaction.getCurrency())),
            targetAccount,
            thirdPartyTransaction.getSecretKey(),
            transactionPurposeFromString(thirdPartyTransaction.getTransactionPurpose())
        )
    );
  }

  public void validateThirdPartyTransaction(ThirdPartyTransaction transaction) throws InstanceNotFoundException {
    if (transaction.getTransactionPurpose() == TransactionPurpose.REQUEST &&
        (accountManagerService.isTransactionTimeFraudulent(transaction.getTargetAccount(), transaction) ||
            accountManagerService.isTransactionDailyAmountFraudulent(transaction.getTargetAccount()))) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Fraudulent behaviour detected! Base account was frozen for safety."));
      accountService.freezeAccount(transaction.getTargetAccount().getId());

    } else if (accountManagerService.isTransactionAmountValid(transaction) && accountManagerService.isAccountsNotFrozen(transaction)) {
      processTransaction(transaction);
      transactionReceiptRepository.save(transaction.acceptAndGenerateReceipt());

    } else if (!accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Account is frozen. Unable to complete the transaction."));

    } else if (!accountManagerService.isTransactionAmountValid(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Invalid amount to transfer."));
    }
    accountService.save(transaction.getTargetAccount());
  }

  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(ThirdPartyTransaction transaction) throws InstanceNotFoundException {
    Account targetAccount = accountService.getById(transaction.getTargetAccount().getId());
    if (transaction.getTransactionPurpose() == TransactionPurpose.REQUEST) {
      targetAccount.setBalance(subtractMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));
    } else if (transaction.getTransactionPurpose() == TransactionPurpose.SEND) {
      targetAccount.setBalance(addMoney(targetAccount.getBalance(), transaction.getConvertedAmount()));
    }
    accountService.save(targetAccount);
    accountManagerService.checkForAlterations(targetAccount);
  }


}
