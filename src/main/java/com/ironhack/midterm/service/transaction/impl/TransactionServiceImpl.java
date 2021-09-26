package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.ironhack.midterm.util.MoneyUtil.*;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== GET Methods ========================================
  public List<Transaction> getAllByAccountId(long accountId) {
    if (accountService.hasAccount(accountId))
      return transactionRepository.findAllByAccountIdJoined(accountId);

    throw new EntityNotFoundException("Account not found.");
  }


  public Transaction getById(long accountId, long transactionId) {
    if (!accountService.hasAccount(accountId)) throw new EntityNotFoundException("Account not found.");

    var transaction = transactionRepository.findByIdJoined(transactionId);
    if (transaction.isPresent()) {
      if ((transaction.get().getBaseAccount() == null || transaction.get().getBaseAccount().getId() != accountId) &&
          transaction.get().getTargetAccount().getId() != accountId)
        throw new IllegalArgumentException("Transaction does not exist in defined account.");

      return transaction.get();
    }
    throw new EntityNotFoundException("Transaction not found.");
  }


  // ======================================== utils Methods ========================================
  // Is Account Frozen
  public boolean isAccountFrozen(Transaction transaction) {
    Account targetAccount = transaction.getTargetAccount();
    Account baseAccount = transaction.getBaseAccount();

    if (targetAccount.getClass() == CheckingAccount.class) {
      if (((CheckingAccount) targetAccount).getAccountStatus() == AccountStatus.FROZEN) return true;
    } else if (targetAccount.getClass() == StudentCheckingAccount.class) {
      if (((StudentCheckingAccount) targetAccount).getAccountStatus() == AccountStatus.FROZEN) return true;
    } else if (targetAccount.getClass() == SavingsAccount.class) {
      if (((SavingsAccount) targetAccount).getAccountStatus() == AccountStatus.FROZEN) return true;
    }

    if (baseAccount != null) {
      if (baseAccount.getClass() == CheckingAccount.class) {
        return ((CheckingAccount) baseAccount).getAccountStatus() == AccountStatus.FROZEN;
      } else if (baseAccount.getClass() == StudentCheckingAccount.class) {
        return ((StudentCheckingAccount) baseAccount).getAccountStatus() == AccountStatus.FROZEN;
      } else if (baseAccount.getClass() == SavingsAccount.class) {
        return ((SavingsAccount) baseAccount).getAccountStatus() == AccountStatus.FROZEN;
      }
    }
    return false;
  }


  // ======================================== utils fraud detection Methods ========================================

  // -------------------- Check if transaction timing is fraudulent --------------------
  public boolean isTransactionTimeFraudulent(Account account) {
    List<Transaction> transactionList = account.getAllTransactionsOrdered();
    if (transactionList.size() <= 1) return false;
    // True if last transaction made was less than a second ago.
    return transactionList.get(1).getOperationDate().plusSeconds(1).isAfter(transactionList.get(0).getOperationDate());
  }


  // -------------------- Check if transaction daily amount is fraudulent --------------------
  public boolean isTransactionDailyAmountFraudulent(Account account) {
    Money totalDayTransaction = lastDayTransactions(account);

    Money dailyMax = allDailyMax(account);
    Money baseMaxTransaction = convertCurrency(account.getBalance(),newMoney("1000"));
    Money dailyMaxTransaction = new Money(dailyMax.getAmount().multiply(new BigDecimal("1.5")), account.getBalance().getCurrency());

    return compareMoney(totalDayTransaction, dailyMaxTransaction) > 0 && // greater than 1.5x max daily transaction
        compareMoney(totalDayTransaction, baseMaxTransaction) > 0;  // greater than 1000€
  }

  // -------------------- utils: last day transaction amount --------------------
  public Money lastDayTransactions(Account account) {
    HashMap<LocalDate, Money> dailyTransactions = dailyTransactions(account);
    Optional<Map.Entry<LocalDate, Money>> lastDailyMoney = dailyTransactions.entrySet().stream().max(Map.Entry.comparingByKey());
    return lastDailyMoney.map(Map.Entry::getValue).orElse(null);
  }

  // -------------------- utils: max transaction amount in one day --------------------
  public Money allDailyMax(Account account) {
    HashMap<LocalDate, Money> dailyTransactions = dailyTransactions(account);
    Optional<Map.Entry<LocalDate, Money>> maxDailyMoney = dailyTransactions.entrySet().stream().max(
        Comparator.comparing((Map.Entry<LocalDate, Money> e) -> e.getValue().getAmount())
    );
    return maxDailyMoney.map(Map.Entry::getValue).orElse(null);
  }

  // -------------------- utils: transaction amount by day--------------------
  public HashMap<LocalDate, Money> dailyTransactions(Account account) {
    HashMap<LocalDate, Money> dailyTransactions = new HashMap<>();
    for (Transaction transaction : account.getAllTransactionsOrdered()) {
      if (!dailyTransactions.containsKey(transaction.getOperationDate().toLocalDate())) {
        dailyTransactions.put(
            transaction.getOperationDate().toLocalDate(),
            convertCurrency(account.getBalance(), transaction.getBaseAmount())
        );
      } else {
        dailyTransactions.put(
            transaction.getOperationDate().toLocalDate(),
            addMoney(dailyTransactions.get(transaction.getOperationDate().toLocalDate()), convertCurrency(account.getBalance(), transaction.getBaseAmount()))
        );
      }
    }
    return dailyTransactions;
  }

}
