package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.transaction.InterestTransaction;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.InterestTransactionRepository;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.service.AccountManagerServiceImpl;
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
  private InterestTransactionRepository interestTransactionRepository;

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountManagerServiceImpl accountManagerService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public InterestTransaction newTransaction(long accountId) throws InstanceNotFoundException, IllegalArgumentException {
    Account account = accountService.getById(accountId);
    if (account.getClass() == SavingsAccount.class) {
      BigDecimal interestRate = ((SavingsAccount) account).getInterestRate();
      Money interestAmount = new Money(account.getBalance().getAmount().multiply(interestRate), account.getBalance().getCurrency());
      return interestTransactionRepository.save(new InterestTransaction(interestAmount, account));

    } else if (account.getClass() == CreditCard.class) {
      BigDecimal interestRate = ((CreditCard) account).getInterestRate().divide(new BigDecimal("12"), 4, RoundingMode.HALF_EVEN);
      Money interestAmount = new Money(account.getBalance().getAmount().multiply(interestRate), account.getBalance().getCurrency());
      return interestTransactionRepository.save(new InterestTransaction(interestAmount, account));

    }
    throw new IllegalArgumentException("Error when using account");
  }


  // ======================================== VALIDATE TRANSACTION Methods ========================================
  public void validateInterestTransaction(InterestTransaction transaction) throws InstanceNotFoundException {
    if (accountManagerService.isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.acceptAndGenerateReceipt());
      processTransaction(transaction);
    } else {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Account is frozen. Unable to add interest rate."));
    }
  }

  // ======================================== PROCESS TRANSACTION Methods ========================================
  public void processTransaction(InterestTransaction transaction) throws InstanceNotFoundException {
    Account account = accountService.getById(transaction.getTargetAccount().getId());
    account.setBalance(addMoney(account.getBalance(), transaction.getConvertedAmount()));
    if (account.getClass() == SavingsAccount.class)
      ((SavingsAccount) account).setLastInterestUpdate(((SavingsAccount) account).getLastInterestUpdate().plusYears(1));
    if (account.getClass() == CreditCard.class)
      ((CreditCard) account).setLastInterestUpdate(((CreditCard) account).getLastInterestUpdate().plusMonths(1));
    accountService.save(account);
    accountManagerService.checkForAlterations(account);
  }

}
