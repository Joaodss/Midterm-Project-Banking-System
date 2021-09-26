package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.repository.account.CreditCardRepository;
import com.ironhack.midterm.service.account.CreditCardService;
import com.ironhack.midterm.service.transaction.InterestTransactionService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import com.ironhack.midterm.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;
import static com.ironhack.midterm.util.MoneyUtil.compareMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Service
public class CreditCardServiceImpl implements CreditCardService {

  @Autowired
  private CreditCardRepository creditCardRepository;

  @Autowired
  private AccountHolderService accountHolderService;

  @Autowired
  private InterestTransactionService interestTransactionService;

  @Autowired
  private PenaltyFeeTransactionService penaltyFeeTransactionService;


  // ======================================== GET ACCOUNT Methods ========================================
  public List<CreditCard> getAll() {
    return creditCardRepository.findAllJoined();
  }


  // ======================================== ADD ACCOUNT Methods ========================================
  public void newAccount(AccountDTO creditCard) throws EntityNotFoundException, IllegalArgumentException {
    // Perform an identity check of both account owners
    AccountHolder[] accountHolders = accountHolderService.findAccountHolders(creditCard);

    CreditCard cc = new CreditCard(newMoney(creditCard.getInitialBalance().toString(), creditCard.getCurrency()), accountHolders[0], accountHolders[1]);
    cc.updateCurrencyValues(); // converts default values if primary balance currency is different.

    creditCardRepository.save(cc);
  }


  public void checkInterestRate(CreditCard creditCard) {
    LocalDate lastInterestDate = creditCard.getLastInterestUpdate();

    if (lastInterestDate.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
      Transaction transaction = interestTransactionService.newTransaction(creditCard.getId());
      interestTransactionService.validateInterestTransaction(transaction);
    }
  }


  public void checkCreditLimit(CreditCard creditCard) {
    LocalDate lastPenaltyFee = creditCard.getLastPenaltyFeeCheck();

    if (compareMoney(creditCard.getBalance(), creditCard.getCreditLimit()) < 0) { // TODO - change here if credit card works the opposite way.

      if (lastPenaltyFee.plusMonths(1).isBefore(dateTimeNow().toLocalDate())) {
        Transaction transaction = penaltyFeeTransactionService.newTransaction(creditCard.getId());
        penaltyFeeTransactionService.validatePenaltyFeeTransaction(transaction);
      } // TODO - Change if order to make the credit card only check the balance once per month. Set to be in the start of the month.
    } else if (creditCard.getLastPenaltyFeeCheck().isBefore(dateTimeNow().toLocalDate().minusMonths(1).minusDays(1))) {
      creditCard.setLastPenaltyFeeCheck(dateTimeNow().toLocalDate().minusMonths(1).minusDays(1));
      creditCardRepository.save(creditCard);
    }
  }

}
