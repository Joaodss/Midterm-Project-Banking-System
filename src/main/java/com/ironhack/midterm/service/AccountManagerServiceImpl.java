package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dao.transaction.*;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.InterestTransactionService;
import com.ironhack.midterm.service.transaction.MaintenanceFeeTransactionService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import static com.ironhack.midterm.util.MoneyUtil.*;
import static com.ironhack.midterm.util.validation.DateTimeUtil.dateTimeNow;

@Service
public class AccountManagerServiceImpl {

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private InterestTransactionService interestTransactionService;

  @Autowired
  private MaintenanceFeeTransactionService maintenanceFeeTransactionService;

  @Autowired
  private PenaltyFeeTransactionService penaltyFeeTransactionService;


  // ============================== Check Account Alterations ==============================
  public void checkForAlterations(Account account) throws InstanceNotFoundException {
    if (account.getClass() == SavingsAccount.class) {
      LocalDate lastInterestDate = ((SavingsAccount) account).getLastInterestUpdate();
      if (lastInterestDate.plusYears(1).isBefore(dateTimeNow().toLocalDate())) {
        InterestTransaction transaction = interestTransactionService.newTransaction(account.getId());
        interestTransactionService.validateInterestTransaction(transaction);
      }
    } else if (account.getClass() == CreditCard.class) {
      LocalDate lastInterestRate = ((CreditCard) account).getLastInterestUpdate();
      if (lastInterestRate.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
        InterestTransaction transaction = interestTransactionService.newTransaction(account.getId());
        interestTransactionService.validateInterestTransaction(transaction);
      }
    }

    if (account.getClass() == CheckingAccount.class) {
      LocalDate lastMaintenanceDate = ((CheckingAccount) account).getLastMaintenanceFee();
      if (lastMaintenanceDate.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
        MaintenanceFeeTransaction transaction = maintenanceFeeTransactionService.newTransaction(account.getId());
        maintenanceFeeTransactionService.validateMaintenanceFeeTransaction(transaction);
      }
    }

    if (account.getClass() == CheckingAccount.class) {
      if (compareMoney(account.getBalance(), ((CheckingAccount) account).getMinimumBalance()) < 0) {
        LocalDate lastPenaltyFee = account.getLastPenaltyFee();
        if (lastPenaltyFee.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
          PenaltyFeeTransaction transaction = penaltyFeeTransactionService.newTransaction(account.getId());
          penaltyFeeTransactionService.validatePenaltyFeeTransaction(transaction);
        }
      }
    } else if (account.getClass() == SavingsAccount.class) {
      if (compareMoney(account.getBalance(), ((SavingsAccount) account).getMinimumBalance()) < 0) {
        LocalDate lastPenaltyFee = account.getLastPenaltyFee();
        if (lastPenaltyFee.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
          PenaltyFeeTransaction transaction = penaltyFeeTransactionService.newTransaction(account.getId());
          penaltyFeeTransactionService.validatePenaltyFeeTransaction(transaction);
        }
      }
    }

  }



  public void validateThirdPartyTransaction(ThirdPartyTransaction transaction) throws InstanceNotFoundException {
    if (transaction.getTransactionPurpose() == TransactionPurpose.REQUEST &&
        !isTransactionTimeNotFraudulent(transaction.getBaseAccount(), transaction) &&
        !isTransactionDailyAmountNotFraudulent(transaction.getBaseAccount(), transaction)) {
      accountService.freezeAccount(transaction.getBaseAccount().getId());
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Fraudulent behaviour detected! Base account was frozen."));
    } else if (isTransactionAmountValid(transaction) && isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.acceptAndGenerateReceipt());

      // TODO - Add service to exchange money

    } else if (isTransactionAmountValid(transaction) && !isAccountsNotFrozen(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Account is frozen. Unable to complete the transaction."));
    } else if (transaction.getTransactionPurpose() == TransactionPurpose.REQUEST && !isTransactionAmountValid(transaction)) {
      transactionReceiptRepository.save(transaction.refuseAndGenerateReceipt("Invalid amount to transfer."));
    }
  }


  // ============================== SPECIFIC VALIDATION ==============================
  // -------------------- Check if transaction is possible --------------------
  // (transfer money <= account balance and account not frozen)
  public boolean isTransactionAmountValid(Transaction transaction) {
    if (transaction.getClass() == ThirdPartyTransaction.class) {
      if (((ThirdPartyTransaction) transaction).getTransactionPurpose() == TransactionPurpose.REQUEST)
        return compareMoney(transaction.getTargetAccount().getBalance(), transaction.getBaseAmount()) >= 0;
    } else if (transaction.getClass() == LocalTransaction.class) {
      return compareMoney(transaction.getBaseAccount().getBalance(), transaction.getBaseAmount()) >= 0;
    } else if (transaction.getClass() == PenaltyFeeTransaction.class ||
        transaction.getClass() == MaintenanceFeeTransaction.class) {
      return compareMoney(transaction.getTargetAccount().getBalance(), transaction.getBaseAmount()) >= 0;
    }
    return true;
  }

  // -------------------- Check if transaction accounts are one frozen --------------------
  // (accounts in transaction not frozen)
  public boolean isAccountsNotFrozen(Transaction transaction) {
    Account targetAccount = transaction.getTargetAccount();
    Account baseAccount = transaction.getBaseAccount();

    if (targetAccount.getClass() == CheckingAccount.class)
      if (((CheckingAccount) targetAccount).getAccountStatus() == AccountStatus.ACTIVE) return true;
    if (targetAccount.getClass() == StudentCheckingAccount.class)
      if (((StudentCheckingAccount) targetAccount).getAccountStatus() == AccountStatus.ACTIVE) return true;
    if (targetAccount.getClass() == SavingsAccount.class)
      if (((SavingsAccount) targetAccount).getAccountStatus() == AccountStatus.ACTIVE) return true;

    if (baseAccount != null) {
      if (baseAccount.getClass() == CheckingAccount.class)
        if (((CheckingAccount) baseAccount).getAccountStatus() == AccountStatus.ACTIVE) return true;
      if (baseAccount.getClass() == StudentCheckingAccount.class)
        if (((StudentCheckingAccount) baseAccount).getAccountStatus() == AccountStatus.ACTIVE) return true;
      if (baseAccount.getClass() == SavingsAccount.class)
        if (((SavingsAccount) baseAccount).getAccountStatus() == AccountStatus.ACTIVE) return true;
    }
    return false;
  }


  // ============================== Fraud Detection ==============================
  // -------------------- Check if transaction timing is fraudulent --------------------
  // (delta_t > 1s)
  public boolean isTransactionTimeNotFraudulent(Account account, Transaction transaction) {
    List<Transaction> transactionList = account.getAllTransactionsOrdered();
    if (transactionList.isEmpty()) return true;
    return transactionList.get(0).getOperationDate().isBefore(transaction.getOperationDate().minusSeconds(1));
  }

  // -------------------- Check if transaction daily amount is fraudulent --------------------
  // (daily amount > 1000 or max daily amount > 150%)
  public boolean isTransactionDailyAmountNotFraudulent(Account account, Transaction transaction) {
    Money totalDayTransaction = convertCurrency(account.getBalance(), transaction.getBaseAmount());
    Entry<LocalDate, Money> lastDayTransactions = lastDailyTransactions(account);
    if (lastDayTransactions.getKey() == transaction.getOperationDate().toLocalDate())
      totalDayTransaction = addMoney(totalDayTransaction, lastDayTransactions.getValue());

    Money dailyMax = dailyMax(account);
    Money initialMaxTransaction = convertCurrency(newMoney("1000"), account.getBalance());
    Money dailyMaxTransaction = new Money(dailyMax.getAmount().multiply(new BigDecimal("1.5")), account.getBalance().getCurrency());

    return compareMoney(dailyMaxTransaction, totalDayTransaction) >= 0 &&
        compareMoney(initialMaxTransaction, totalDayTransaction) >= 0;
  }

  public Entry<LocalDate, Money> lastDailyTransactions(Account account) {
    HashMap<LocalDate, Money> dailyTransactions = dailyTransactions(account);
    Optional<Entry<LocalDate, Money>> lastDailyMoney = dailyTransactions.entrySet().stream().max(Entry.comparingByKey());
    return lastDailyMoney.orElse(null);
  }

  public Money dailyMax(Account account) {
    HashMap<LocalDate, Money> dailyTransactions = dailyTransactions(account);
    Optional<Entry<LocalDate, Money>> maxDailyMoney = dailyTransactions.entrySet().stream().max(
        Comparator.comparing((Entry<LocalDate, Money> e) -> e.getValue().getAmount())
    );
    return maxDailyMoney.map(Entry::getValue).orElse(null);
  }

  public HashMap<LocalDate, Money> dailyTransactions(Account account) {
    HashMap<LocalDate, Money> dailyTransactions = new HashMap<>();
    List<Transaction> allTransactions = account.getAllTransactionsOrdered();
    for (Transaction t : allTransactions) {
      if (!dailyTransactions.containsKey(t.getOperationDate().toLocalDate())) {
        dailyTransactions.put(t.getOperationDate().toLocalDate(), convertCurrency(account.getBalance(), t.getBaseAmount()));
      } else {
        dailyTransactions.put(
            t.getOperationDate().toLocalDate(),
            addMoney(dailyTransactions.get(t.getOperationDate().toLocalDate()), convertCurrency(account.getBalance(), t.getBaseAmount())
            )
        );
      }
    }
    return dailyTransactions;
  }


}
