package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.repository.account.SavingsAccountRepository;
import com.ironhack.midterm.service.account.SavingsAccountService;
import com.ironhack.midterm.service.transaction.InterestTransactionService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import com.ironhack.midterm.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;
import static com.ironhack.midterm.util.MoneyUtil.compareMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Service
public class SavingsAccountServiceImpl implements SavingsAccountService {

  @Autowired
  private SavingsAccountRepository savingsAccountRepository;

  @Autowired
  private AccountHolderService accountHolderService;

  @Autowired
  private InterestTransactionService interestTransactionService;

  @Autowired
  private PenaltyFeeTransactionService penaltyFeeTransactionService;


  // ======================================== GET ACCOUNT Methods ========================================
  public List<SavingsAccount> getAll() {
    return savingsAccountRepository.findAllJoined();
  }

  // ======================================== ADD ACCOUNT Methods ========================================
  public void newAccount(AccountDTO savingsAccount) throws EntityNotFoundException, IllegalArgumentException, NoSuchAlgorithmException {
    // Perform an identity check of both account owners
    AccountHolder[] accountHolders = accountHolderService.findAccountHolders(savingsAccount);

    SavingsAccount sa = new SavingsAccount(newMoney(savingsAccount.getInitialBalance().toString(), savingsAccount.getCurrency()), accountHolders[0], accountHolders[1]);
    sa.updateCurrencyValues();

    savingsAccountRepository.save(sa);
  }


  public void checkInterestRate(SavingsAccount savingsAccount) {
    LocalDate lastInterestDate = savingsAccount.getLastInterestUpdate();

    if (savingsAccount.getAccountStatus() == AccountStatus.ACTIVE &&
        lastInterestDate.plusYears(1).isBefore(dateTimeNow().toLocalDate()))
      interestTransactionService.newTransaction(savingsAccount.getId());
  }


  public void checkMinimumBalance(SavingsAccount savingsAccount) {
    LocalDate lastPenaltyFee = savingsAccount.getLastPenaltyFeeCheck();

    if (savingsAccount.getAccountStatus() == AccountStatus.ACTIVE &&
        compareMoney(savingsAccount.getBalance(), savingsAccount.getMinimumBalance()) < 0) {

      if (lastPenaltyFee.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
        penaltyFeeTransactionService.newTransaction(savingsAccount.getId());
      }
    } else if (savingsAccount.getLastPenaltyFeeCheck().isBefore(dateTimeNow().toLocalDate().minusMonths(1).minusDays(1))) {
      savingsAccount.setLastPenaltyFeeCheck(dateTimeNow().toLocalDate().minusMonths(1).minusDays(1));
      savingsAccountRepository.save(savingsAccount);
    }
  }

}
