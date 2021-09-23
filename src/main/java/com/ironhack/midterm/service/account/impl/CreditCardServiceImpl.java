package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.repository.account.CreditCardRepository;
import com.ironhack.midterm.service.account.CreditCardService;
import com.ironhack.midterm.service.user.AccountHolderService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Service
public class CreditCardServiceImpl implements CreditCardService {

  @Autowired
  private CreditCardRepository creditCardRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private AccountHolderService accountHolderService;


  // ======================================== GET ACCOUNT Methods ========================================
  public List<CreditCard> getAll() {
    return creditCardRepository.findAllJoined();
  }


  // ======================================== ADD ACCOUNT Methods ========================================
  public void newAccount(AccountDTO creditCard) throws InstanceNotFoundException, IllegalArgumentException {
    // Perform an identity check of both account owners
    AccountHolder[] accountHolders = accountHolderService.getAccountHolders(creditCard, accountHolderService, userService);

    CreditCard cc = new CreditCard(newMoney(creditCard.getInitialBalance().toString(), creditCard.getCurrency()), accountHolders[0], accountHolders[1]);
    cc.updateCurrencyValues(); // converts default values if primary balance currency is different.

    creditCardRepository.save(cc);
  }


}
