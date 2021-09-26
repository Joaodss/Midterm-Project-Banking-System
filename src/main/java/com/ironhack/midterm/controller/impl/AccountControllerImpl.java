package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.AccountController;
import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.dto.AccountEditDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.service.account.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.security.auth.login.LoginException;
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


  // ======================================== GET ACCOUNT Methods ========================================
  // -------------------- All Accounts [ADMIN] / User Specific Accounts [Specific USER] --------------------
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Account> getAccounts(Authentication auth) {
    try {
      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
        return accountService.getAll();
      } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
        return accountService.getAllByUsername(auth.getName());
      } else throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Account by Id [ADMIN] / User Specific Account by Id [Specific USER] --------------------
  @GetMapping("/{account_id}")
  @ResponseStatus(HttpStatus.OK)
  public Account getAccountById(@PathVariable("account_id") long id) {
    try {
      return accountService.getById(id);
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found or not associated with your account.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Account Balance by Id [ADMIN] / User Specific Account Balance by Id [Specific USER] --------------------
  @GetMapping("/{account_id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money getAccountBalanceById(@PathVariable("account_id") long id) {
    try {
      return accountService.getById(id).getBalance();
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
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


  // ======================================== POST ACCOUNT Methods ========================================
  // -------------------- New Checking Account [ADMIN] --------------------
  @PostMapping("/new_checking_account")
  @ResponseStatus(HttpStatus.CREATED)
  public void createCheckingAccount(@RequestBody @Valid AccountDTO checkingAccount) {
    try {
      checkingAccountService.newAccount(checkingAccount);
    } catch (EntityNotFoundException e1) {
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
      savingsAccountService.newAccount(savingsAccount);
    } catch (EntityNotFoundException e1) {
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
      creditCardService.newAccount(creditCard);
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and / or username where not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id and username did not match for the same entity");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== PATCH Methods ========================================
  @PatchMapping("/edit/account/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void editAccount(@PathVariable("id") long id, @RequestBody @Valid AccountEditDTO accountEdit) {
    try {
      accountService.edit(id, accountEdit);
    } catch (IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
