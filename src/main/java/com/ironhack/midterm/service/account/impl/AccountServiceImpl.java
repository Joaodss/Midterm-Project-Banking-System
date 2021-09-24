package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.AccountEditDTO;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.account.AccountRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.Currency;
import java.util.List;

import static com.ironhack.midterm.util.EnumsUtil.accountStatusFromString;
import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private UserService userService;


  // ======================================== GET Methods ========================================
  public List<Account> getAll() {
    return accountRepository.findAllJoined();
  }

  public Account getById(Long id) throws InstanceNotFoundException {
    var account = accountRepository.findByIdJoined(id);
    if (account.isPresent()) return account.get();
    throw new InstanceNotFoundException();
  }

  public Money getBalanceById(long id) throws InstanceNotFoundException {
    var account = accountRepository.findByIdJoined(id);
    if (account.isPresent()) return account.get().getBalance();
    throw new InstanceNotFoundException();
  }

  public List<Account> getAllByUsername(String username) {
    return accountRepository.findAllByUsernameJoined(username);
  }

  public Account getByUsernameAndId(String username, long id) throws InstanceNotFoundException {
    var account = accountRepository.findByUsernameAndIdJoined(username, id);
    if (account.isPresent()) return account.get();
    throw new InstanceNotFoundException();
  }

  public Money getBalanceByUsernameAndId(String username, long id) throws InstanceNotFoundException {
    var account = accountRepository.findByUsernameAndIdJoined(username, id);
    if (account.isPresent()) return account.get().getBalance();
    throw new InstanceNotFoundException();
  }


  public void freezeAccount(long id) throws InstanceNotFoundException {
    var account = getById(id);
    if (account.getClass() == CheckingAccount.class)
      ((CheckingAccount) account).setAccountStatus(AccountStatus.FROZEN);
    if (account.getClass() == StudentCheckingAccount.class)
      ((StudentCheckingAccount) account).setAccountStatus(AccountStatus.FROZEN);
    if (account.getClass() == SavingsAccount.class)
      ((SavingsAccount) account).setAccountStatus(AccountStatus.FROZEN);
    save(account);
  }

  public void unFreezeAccount(long id) throws InstanceNotFoundException {
    var account = getById(id);
    if (account.getClass() == CheckingAccount.class)
      ((CheckingAccount) account).setAccountStatus(AccountStatus.ACTIVE);
    if (account.getClass() == StudentCheckingAccount.class)
      ((StudentCheckingAccount) account).setAccountStatus(AccountStatus.ACTIVE);
    if (account.getClass() == SavingsAccount.class)
      ((SavingsAccount) account).setAccountStatus(AccountStatus.ACTIVE);
    save(account);
  }


  // ============================== Save Account ==============================
  public void save(Account account) {
    accountRepository.save(account);
  }

  public void edit(long id, AccountEditDTO accountEdit) throws InstanceNotFoundException {
    Account account = getById(id);

    if (accountEdit.getPrimaryOwnerUsername() != null) {
      User primaryUser = userService.getByUsername(accountEdit.getPrimaryOwnerUsername());
      if (primaryUser.getClass() == AccountHolder.class) account.setPrimaryOwner((AccountHolder) primaryUser);
    }

    if (accountEdit.getSecondaryOwnerUsername() != null) {
      User secondaryUser = userService.getByUsername(accountEdit.getSecondaryOwnerUsername());
      if (secondaryUser.getClass() == AccountHolder.class) account.setSecondaryOwner((AccountHolder) secondaryUser);
    }

    // ========== (Student) Checking Accounts ==========
    if (account.getClass() == CheckingAccount.class || account.getClass() == StudentCheckingAccount.class) {
      // Status
      if (accountEdit.getAccountStatus() != null)
        ((CheckingAccount) account).setAccountStatus(accountStatusFromString(accountEdit.getAccountStatus()));

      // Currency
      if (accountEdit.getCurrency() != null) {
        account.setBalance(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), account.getBalance()));
        account.setPenaltyFee(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), account.getPenaltyFee()));
        ((CheckingAccount) account).setMinimumBalance(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), ((CheckingAccount) account).getMinimumBalance()));
        ((CheckingAccount) account).setMonthlyMaintenanceFee(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), ((CheckingAccount) account).getMonthlyMaintenanceFee()));
      }

      // Minimum Balance
      if (accountEdit.getMinimumBalance() != null && account.getClass() != StudentCheckingAccount.class)
        ((CheckingAccount) account).setMinimumBalance(new Money(accountEdit.getMinimumBalance(), ((CheckingAccount) account).getMinimumBalance().getCurrency()));

      // Maintenance Fee
      if (accountEdit.getMonthlyMaintenanceFee() != null && account.getClass() != StudentCheckingAccount.class)
        ((CheckingAccount) account).setMonthlyMaintenanceFee(new Money(accountEdit.getMonthlyMaintenanceFee(), ((CheckingAccount) account).getMonthlyMaintenanceFee().getCurrency()));

      if (accountEdit.getLastMaintenanceFee() != null)
        ((CheckingAccount) account).setLastMaintenanceFee(accountEdit.getLastMaintenanceFee());


      // ========== Savings Accounts ==========
    } else if (account.getClass() == SavingsAccount.class) {
      // Status
      if (accountEdit.getAccountStatus() != null)
        ((SavingsAccount) account).setAccountStatus(accountStatusFromString(accountEdit.getAccountStatus()));

      // Currency
      if (accountEdit.getCurrency() != null) {
        account.setBalance(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), account.getBalance()));
        account.setPenaltyFee(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), account.getPenaltyFee()));
        ((SavingsAccount) account).setMinimumBalance(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), ((SavingsAccount) account).getMinimumBalance()));
      }

      // Minimum Balance
      if (accountEdit.getMinimumBalance() != null) {
        Money minMinimumBalanceConverted = convertCurrency(((SavingsAccount) account).getMinimumBalance(), newMoney("100"));
        if (accountEdit.getMinimumBalance().compareTo(minMinimumBalanceConverted.getAmount()) >= 0) {
          ((SavingsAccount) account).setMinimumBalance(new Money(accountEdit.getMinimumBalance(), ((SavingsAccount) account).getMinimumBalance().getCurrency()));
        } else
          throw new IllegalArgumentException("Savings account minimum balance must be greater than " + minMinimumBalanceConverted + ".");
      }

      // Interest Rate
      if (accountEdit.getSavingsAccountInterestRate() != null)
        ((SavingsAccount) account).setInterestRate(accountEdit.getSavingsAccountInterestRate());

      if (accountEdit.getLastInterestUpdate() != null)
        ((SavingsAccount) account).setLastInterestUpdate(accountEdit.getLastInterestUpdate());


      // ========== Credit Cards ==========
    } else if (account.getClass() == CreditCard.class) {
      // Currency
      if (accountEdit.getCurrency() != null) {
        account.setBalance(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), account.getBalance()));
        account.setPenaltyFee(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), account.getPenaltyFee()));
        ((CreditCard) account).setCreditLimit(convertCurrency(Currency.getInstance(accountEdit.getCurrency()), ((CreditCard) account).getCreditLimit()));
      }

      // Credit Limit
      if (accountEdit.getMinimumBalance() != null) {
        Money maxCreditLimitConverted = convertCurrency(((CreditCard) account).getCreditLimit(), newMoney("100000"));
        if (accountEdit.getCreditLimit().compareTo(maxCreditLimitConverted.getAmount()) <= 0) {
          ((CreditCard) account).setCreditLimit(new Money(accountEdit.getCreditLimit(), ((CreditCard) account).getCreditLimit().getCurrency()));
        } else
          throw new IllegalArgumentException("Credit limit must be lower than " + maxCreditLimitConverted + ".");
      }

      // Interest Rate
      if (accountEdit.getCreditCardInterestRate() != null)
        ((CreditCard) account).setInterestRate(accountEdit.getCreditCardInterestRate());

      if (accountEdit.getLastInterestUpdate() != null)
        ((CreditCard) account).setLastInterestUpdate(accountEdit.getLastInterestUpdate());

    }

    // Account Balance
    if (accountEdit.getAccountBalance() != null)
      account.setBalance(new Money(accountEdit.getAccountBalance(), account.getBalance().getCurrency()));

    // Penalty Fee
    if (accountEdit.getPenaltyFee() != null)
      account.setPenaltyFee(new Money(accountEdit.getPenaltyFee(), account.getPenaltyFee().getCurrency()));

    if (accountEdit.getLastPenaltyFee() != null) account.setLastPenaltyFeeCheck(accountEdit.getLastPenaltyFee());

    save(account);

  }


}
