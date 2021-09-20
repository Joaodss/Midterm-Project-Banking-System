package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.AccountController;
import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.service.account.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceNotFoundException;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountControllerImpl implements AccountController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private CheckingAccountService checkingAccountService;

  @Autowired
  private StudentCheckingAccountService studentCheckingAccountService;

  @Autowired
  private SavingsAccountService savingsAccountService;

  @Autowired
  private CreditCardService creditCardService;


  // ======================================== GET Methods ========================================
  // -------------------- All Accounts [ADMIN] --------------------
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Account> getAccounts() {
    try {
      return accountService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- All Checking Accounts [ADMIN] --------------------
  @GetMapping("/checking_accounts")
  @ResponseStatus(HttpStatus.OK)
  public List<CheckingAccount> getCheckingAccounts() {
    try {
      return checkingAccountService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- All Student Checking Accounts [ADMIN] --------------------
  @GetMapping("/student_checking_accounts")
  @ResponseStatus(HttpStatus.OK)
  public List<StudentCheckingAccount> getStudentCheckingAccounts() {
    try {
      return studentCheckingAccountService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- All Savings Accounts [ADMIN] --------------------
  @GetMapping("/savings_accounts")
  @ResponseStatus(HttpStatus.OK)
  public List<SavingsAccount> getSavingsAccounts() {
    try {
      return savingsAccountService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- All Credit Cards [ADMIN] --------------------
  @GetMapping("/credit_cards")
  @ResponseStatus(HttpStatus.OK)
  public List<CreditCard> getCreditCards() {
    try {
      return creditCardService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Accounts by Id [ADMIN] --------------------
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
  @PostMapping("/new_checking_account")
  @ResponseStatus(HttpStatus.CREATED)
  public void createCheckingAccount(@RequestBody @Valid AccountDTO checkingAccount) {
    try {
      checkingAccountService.newUser(checkingAccount);
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and / or username where not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and username did not match for the same entity");
    } catch (NoSuchAlgorithmException e3) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "An error occurred with the secret key generation algorithm. Please retry. If the error persists contact us.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- New Savings Account [ADMIN] --------------------
  @PostMapping("/new_savings_account")
  @ResponseStatus(HttpStatus.CREATED)
  public void createSavingsAccount(@RequestBody @Valid AccountDTO savingsAccount) {
    try {
      savingsAccountService.newUser(savingsAccount);
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and / or username where not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and username did not match for the same entity");
    } catch (NoSuchAlgorithmException e3) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "An error occurred with the secret key generation algorithm. Please retry. If the error persists contact us.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- New Credit Card [ADMIN] --------------------
  @PostMapping("/new_credit_card")
  @ResponseStatus(HttpStatus.CREATED)
  public void createCreditCard(@RequestBody @Valid AccountDTO creditCard) {
    try {
      creditCardService.newUser(creditCard);
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and / or username where not found.");
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
