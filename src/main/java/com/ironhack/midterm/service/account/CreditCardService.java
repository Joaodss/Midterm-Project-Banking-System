package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dto.AccountDTO;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface CreditCardService {

  // ======================================== GET ACCOUNT Methods ========================================
  List<CreditCard> getAll();


  // ======================================== ADD ACCOUNT Methods ========================================
  void newAccount(AccountDTO creditCard) throws InstanceNotFoundException, IllegalArgumentException;


}
