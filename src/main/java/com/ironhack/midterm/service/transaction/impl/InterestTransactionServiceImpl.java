package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.InterestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
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
  private AccountManagerService accountManagerService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public Transaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException {
    Account account = accountService.getById(accountId);
    if (account.getClass() == SavingsAccount.class) {
      BigDecimal interestRate = ((SavingsAccount) account).getInterestRate();
      Money interestAmount = new Money(account.getBalance().getAmount().multiply(interestRate), account.getBalance().getCurrency());
      return transactionRepository.save(new Transaction(interestAmount, account));

    } else if (account.getClass() == CreditCard.class) {
      BigDecimal interestRate = ((CreditCard) account).getInterestRate().divide(new BigDecimal("12"), 4, RoundingMode.HALF_EVEN);
      Money interestAmount = new Money(account.getBalance().getAmount().multiply(interestRate), account.getBalance().getCurrency());
      return transactionRepository.save(new Transaction(interestAmount, account));

    }
    throw new IllegalArgumentException("Error when using account");
  }


  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validateInterestTransaction(Transaction transaction) throws InstanceNotFoundException {
    if (accountManagerService.isAccountsNotFrozen(transaction)) {
      receiptRepository.save(transaction.generateInterestTransactionReceipt(true));
      processTransaction(transaction);
    } else {
      receiptRepository.save(transaction.generateInterestTransactionReceipt(false, "Account is frozen. Unable to add interest rate."));
    }
    accountService.save(transaction.getTargetAccount());
  }

  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(Transaction transaction) throws InstanceNotFoundException {
    Account account = accountService.getById(transaction.getTargetAccount().getId());
    account.setBalance(addMoney(account.getBalance(), transaction.getConvertedAmount()));
    if (account.getClass() == SavingsAccount.class)
      ((SavingsAccount) account).setLastInterestUpdate(((SavingsAccount) account).getLastInterestUpdate().plusYears(1));
    if (account.getClass() == CreditCard.class)
      ((CreditCard) account).setLastInterestUpdate(((CreditCard) account).getLastInterestUpdate().plusMonths(1));
    accountService.save(account);
    accountManagerService.checkForAlterations(account);
  }

  // (Transfer money will always be greater because it is adding)
  public boolean isTransactionAmountValid(Transaction transaction) {
    return true;
  }

}
