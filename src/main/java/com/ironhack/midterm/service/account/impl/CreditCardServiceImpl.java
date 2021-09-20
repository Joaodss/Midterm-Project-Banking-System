package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.CreditCardDTO;
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
  public void newUser(CreditCardDTO creditCard) throws InstanceNotFoundException {
    // Check if usernames exists in Account Holders
    if (!accountHolderService.isUsernamePresent(creditCard.getPrimaryOwnerUsername()))
      throw new InstanceNotFoundException("Primary owner user not found by username.");
    if (creditCard.getSecondaryOwnerUsername() != null && !accountHolderService.isUsernamePresent(creditCard.getSecondaryOwnerUsername()))
      throw new InstanceNotFoundException("Secondary owner user not found by username.");

    // Check if username and id match
    try {
      if (!userService.getById(creditCard.getPrimaryOwnerId()).getUsername().equals(creditCard.getPrimaryOwnerUsername()))
        throw new IllegalArgumentException("Primary owner's username and id do not match.");
    } catch (InstanceNotFoundException e1) {
      throw new InstanceNotFoundException("Primary owner's id not found.");
    }
    try {
      if (creditCard.getSecondaryOwnerId() != null && !userService.getById(creditCard.getSecondaryOwnerId()).getUsername().equals(creditCard.getSecondaryOwnerUsername()))
        throw new IllegalArgumentException("Secondary owner's username and id do not match.");
    } catch (InstanceNotFoundException e1) {
      throw new InstanceNotFoundException("Secondary owner's id not found.");
    }

    AccountHolder pah = accountHolderService.getByUsername(creditCard.getPrimaryOwnerUsername());
    AccountHolder sah = null;
    if (creditCard.getSecondaryOwnerId() != null && creditCard.getSecondaryOwnerUsername() != null)
      sah = accountHolderService.getByUsername(creditCard.getSecondaryOwnerUsername());

    CreditCard cc = new CreditCard(newMoney(creditCard.getInitialBalance().toString(), creditCard.getCurrency()), pah, sah);
    cc.updateCurrencyValues(); // converts default values if primary balance currency is different.

    creditCardRepository.save(cc);
  }


}
