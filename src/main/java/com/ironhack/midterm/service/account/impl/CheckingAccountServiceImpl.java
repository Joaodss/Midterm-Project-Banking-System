package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.repository.account.CheckingAccountRepository;
import com.ironhack.midterm.service.account.CheckingAccountService;
import com.ironhack.midterm.service.account.StudentCheckingAccountService;
import com.ironhack.midterm.service.transaction.MaintenanceFeeTransactionService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import com.ironhack.midterm.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;
import static com.ironhack.midterm.util.MoneyUtil.compareMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Service
public class CheckingAccountServiceImpl implements CheckingAccountService {

  @Autowired
  private CheckingAccountRepository checkingAccountRepository;

  @Autowired
  private AccountHolderService accountHolderService;

  @Autowired
  private StudentCheckingAccountService studentCheckingAccountService;

  @Autowired
  private MaintenanceFeeTransactionService maintenanceFeeTransactionService;

  @Autowired
  private PenaltyFeeTransactionService penaltyFeeTransactionService;


  // ======================================== GET ACCOUNT Methods ========================================
  public List<CheckingAccount> getAll() {
    ArrayList<CheckingAccount> checkingAccounts = new ArrayList<>(checkingAccountRepository.findAllJoined());
    ArrayList<StudentCheckingAccount> studentCheckingAccounts = new ArrayList<>(studentCheckingAccountService.getAll());
    checkingAccounts.removeAll(studentCheckingAccounts);
    return checkingAccounts;
  }

  // ======================================== ADD ACCOUNT Methods ========================================
  public void newAccount(AccountDTO checkingAccount) throws EntityNotFoundException, IllegalArgumentException, NoSuchAlgorithmException {
    // Perform an identity check of both account owners
    AccountHolder[] accountHolders = accountHolderService.findAccountHolders(checkingAccount);

    if (accountHolders[0].getDateOfBirth().plusYears(25).isBefore(LocalDate.now())) {
      // older than 24 years (25 years old or more) (birthdate + 25 < now)
      CheckingAccount ca = new CheckingAccount(newMoney(checkingAccount.getInitialBalance().toString(), checkingAccount.getCurrency()), accountHolders[0], accountHolders[1]);
      ca.updateCurrencyValues();
      checkingAccountRepository.save(ca);
    } else {
      // younger than 24 years (24 years old or less) (birthdate + 25 > now)
      StudentCheckingAccount sca = new StudentCheckingAccount(newMoney(checkingAccount.getInitialBalance().toString(), checkingAccount.getCurrency()), accountHolders[0], accountHolders[1]);
      sca.updateCurrencyValues();
      studentCheckingAccountService.newAccount(sca);
    }
  }


  public void checkMaintenanceFee(CheckingAccount checkingAccount) {
    LocalDate lastMaintenanceDate = checkingAccount.getLastMaintenanceFee();

    if (checkingAccount.getAccountStatus() == AccountStatus.ACTIVE &&
        lastMaintenanceDate.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
      maintenanceFeeTransactionService.newTransaction(checkingAccount.getId());
    }
  }


  public void checkMinimumBalance(CheckingAccount checkingAccount) {
    LocalDate lastPenaltyFee = checkingAccount.getLastPenaltyFeeCheck();

    if (checkingAccount.getAccountStatus() == AccountStatus.ACTIVE &&
        compareMoney(checkingAccount.getBalance(), checkingAccount.getMinimumBalance()) < 0) {

      if (lastPenaltyFee.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
        penaltyFeeTransactionService.newTransaction(checkingAccount.getId());
      }
    } else if (checkingAccount.getLastPenaltyFeeCheck().isBefore(dateTimeNow().toLocalDate().minusMonths(1).minusDays(1))) {
      checkingAccount.setLastPenaltyFeeCheck(dateTimeNow().toLocalDate().minusMonths(1).minusDays(1));
      checkingAccountRepository.save(checkingAccount);
    }
  }


}
