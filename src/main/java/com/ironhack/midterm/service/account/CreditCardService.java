package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dto.AccountDTO;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface CreditCardService {

  // ======================================== get Methods ========================================
  List<CreditCard> getAll();

  // ======================================== new Methods ========================================
  void newAccount(AccountDTO creditCard) throws InstanceNotFoundException, IllegalArgumentException;

  // ======================================== update balance Methods ========================================

}
