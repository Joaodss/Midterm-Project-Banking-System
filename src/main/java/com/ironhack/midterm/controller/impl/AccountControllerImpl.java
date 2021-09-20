package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.AccountController;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dto.CreditCardDTO;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.account.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountControllerImpl implements AccountController {

  @Autowired
  private AccountService accountService;

  //  @Autowired
//  private AdminService adminService;
//
//  @Autowired
//  private AccountHolderService accountHolderService;
//
  @Autowired
  private CreditCardService creditCardService;


  // ======================================== GET Methods ========================================
  // -------------------- All Users [ADMIN] --------------------
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Account> getAccounts() {
    try {
      return accountService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

//  public List<CheckingAccount> getCheckingAccounts() {
//
//  }
//
//  public List<StudentCheckingAccount> getStudentCheckingAccounts() {
//
//  }
//
//  public List<SavingsAccount> getSavingsAccounts() {
//
//  }

  @GetMapping("/credit_cards")
  @ResponseStatus(HttpStatus.OK)
  public List<CreditCard> getCreditCards() {
    try {
      return creditCardService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Account getAccountById(@PathVariable("id") long id) {
    try {
      return accountService.getById(id);
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== POST Methods ========================================
  // -------------------- New Checking Account [ADMIN] --------------------
//  public void createCheckingAccount(CheckingAccountDTO checkingAccount) {
//
//  }
//
//  // -------------------- New Savings Account [ADMIN] --------------------
//  public void createSavingsAccount(SavingsAccountDTO savingsAccount) {
//
//  }

  // -------------------- New Credit Card [ADMIN] --------------------
  @PostMapping("/new_credit_card")
  @ResponseStatus(HttpStatus.CREATED)
  public void createCreditCard(@RequestBody @Valid CreditCardDTO creditCard) {
    try {
      creditCardService.newUser(creditCard);
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and/or username where not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and username did not match for the same entity");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
