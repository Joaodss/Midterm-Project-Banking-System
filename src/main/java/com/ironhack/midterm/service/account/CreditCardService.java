package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dto.CreditCardDTO;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

public interface CreditCardService {

  // ======================================== GET ACCOUNT Methods ========================================
  List<CreditCard> getAll();


  // ======================================== ADD ACCOUNT Methods ========================================
  void newUser(CreditCardDTO creditCard) throws InstanceNotFoundException;


}
