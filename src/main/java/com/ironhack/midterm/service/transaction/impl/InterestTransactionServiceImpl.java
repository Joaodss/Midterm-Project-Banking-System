package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.InterestTransactionService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.ironhack.midterm.util.MoneyUtil.addMoney;

@Service
public class InterestTransactionServiceImpl implements InterestTransactionService {

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

    Money interestAmount;
    //Savings Account
    if (account.getClass() == SavingsAccount.class) {
      BigDecimal interestRate = ((SavingsAccount) account).getInterestRate();
      interestAmount = new Money(account.getBalance().getAmount().multiply(interestRate), account.getBalance().getCurrency());

      //Credit Card
    } else if (account.getClass() == CreditCard.class) {
      BigDecimal interestRate = ((CreditCard) account).getInterestRate().divide(new BigDecimal("12"), 4, RoundingMode.HALF_EVEN);
      interestAmount = new Money(account.getBalance().getAmount().multiply(interestRate), account.getBalance().getCurrency());

      //Others
    } else throw new IllegalArgumentException("Error when using account");

    Transaction transaction = transactionRepository.save(
        new Transaction(
            interestAmount,
            account
        )
    );
    validateTransaction(transaction);
    accountService.updateBalance(account);
  }


  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validateTransaction(Transaction transaction) {
    // Check if frozen.
    if (transactionService.isAccountFrozen(transaction)) {
      receiptRepository.save(transaction.generateInterestTransactionReceipt(false, "Account is frozen. Unable to add interest rate."));

      // If there are no constrains, accept and process transaction.
    } else {
      processTransaction(transaction);
      receiptRepository.save(transaction.generateInterestTransactionReceipt(true));
    }
    accountService.save(transaction.getTargetAccount());
  }

  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) {
    Account account = accountService.getById(transaction.getTargetAccount().getId());

    account.setBalance(addMoney(account.getBalance(), transaction.getConvertedAmount()));

    // update next interest date
    if (account.getClass() == SavingsAccount.class) {
      ((SavingsAccount) account).setLastInterestUpdate(((SavingsAccount) account).getLastInterestUpdate().plusYears(1));
    } else if (account.getClass() == CreditCard.class) {
      ((CreditCard) account).setLastInterestUpdate(((CreditCard) account).getLastInterestUpdate().plusMonths(1));
    }
    accountService.save(account);
  }

}
