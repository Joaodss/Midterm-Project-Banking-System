package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dto.AccountDTO;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface CreditCardService {

  // ======================================== get Methods ========================================
  List<CreditCard> getAll();

  // ======================================== new Methods ========================================
  void newAccount(AccountDTO creditCard) throws EntityNotFoundException, IllegalArgumentException;

  // ======================================== update balance Methods ========================================
  void checkInterestRate(CreditCard savingsAccount);

  void checkCreditLimit(CreditCard savingsAccount);

}
