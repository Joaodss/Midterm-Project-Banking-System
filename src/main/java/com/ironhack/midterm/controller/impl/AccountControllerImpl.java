package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.AccountController;
import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.service.account.*;
import com.ironhack.midterm.service.transaction.TransactionReceiptService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceNotFoundException;
import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
  private TransactionService transactionService;


  @Autowired
  private TransactionReceiptService transactionReceiptService;

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
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Account by Id [ADMIN] / User Specific Account by Id [Specific USER] --------------------
  @GetMapping("/{account_id}")
  @ResponseStatus(HttpStatus.OK)
  public Account getAccountById(Authentication auth, @PathVariable("account_id") long id) {
    try {
      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
        return accountService.getById(id);
      } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
        return accountService.getByUsernameAndId(auth.getName(), id);
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Account Balance by Id [ADMIN] / User Specific Account Balance by Id [Specific USER] --------------------
  @GetMapping("/{account_id}/balance")
  @ResponseStatus(HttpStatus.OK)
  public Money getAccountBalanceById(Authentication auth, @PathVariable("account_id") long id) {
    try {
      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
        return accountService.getBalanceById(id);
      } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
        return accountService.getBalanceByUsernameAndId(auth.getName(), id);
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
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

  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions")
  @ResponseStatus(HttpStatus.OK)
  public List<Transaction> getTransactions(Authentication auth, @PathVariable("account_id") long id) {
    try {
      Account account = accountService.getById(id);
      List<String> validUsernames = new ArrayList<>();
      validUsernames.add(account.getPrimaryOwner().getUsername());
      if (account.getSecondaryOwner() != null) validUsernames.add(account.getSecondaryOwner().getUsername());

      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) ||
          validUsernames.contains(auth.getName())) {
        return transactionService.getAllByAccountId(id);
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- User Specific Transactions by Date Range [ADMIN / Specific USER] --------------------
//  @GetMapping(value = "/{account_id}/transactions")
//  @ResponseStatus(HttpStatus.OK)
//  public List<Transaction> getTransactionsByDateRange(Authentication auth, @PathVariable("account_id") long id, @RequestParam("start_date") Optional<String> startDate, @RequestParam("end_date") Optional<String> endDate) {
//
//  }

  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/transactions/{transaction_id}")
  @ResponseStatus(HttpStatus.OK)
  public Transaction getTransactionsById(Authentication auth, @PathVariable("transaction_id") long transactionId) {
    try {
      Transaction transaction = transactionService.getById(transactionId);
      List<String> validUsernames = new ArrayList<>();
      validUsernames.add(transaction.getTargetAccount().getPrimaryOwner().getUsername());
      if (transaction.getTargetAccount().getSecondaryOwner() != null)
        validUsernames.add(transaction.getTargetAccount().getSecondaryOwner().getUsername());
      if (transaction.getBaseAccount().getPrimaryOwner() != null)
        validUsernames.add(transaction.getBaseAccount().getPrimaryOwner().getUsername());
      if (transaction.getBaseAccount().getSecondaryOwner() != null)
        validUsernames.add(transaction.getBaseAccount().getSecondaryOwner().getUsername());

      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) ||
          validUsernames.contains(auth.getName())) {
        return transaction;
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/receipts")
  @ResponseStatus(HttpStatus.OK)
  public List<TransactionReceipt> getReceipts(Authentication auth, @PathVariable("account_id") long id) {
    try {
      Account account = accountService.getById(id);
      List<String> validUsernames = new ArrayList<>();
      validUsernames.add(account.getPrimaryOwner().getUsername());
      if (account.getSecondaryOwner() != null) validUsernames.add(account.getSecondaryOwner().getUsername());

      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) ||
          validUsernames.contains(auth.getName())) {
        return transactionReceiptService.getAllByAccountId(id);
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- User Specific Transactions by Date Range [ADMIN / Specific USER] --------------------
//  @GetMapping(value = "/{account_id}/receipts")
//  @ResponseStatus(HttpStatus.OK)
//  public List<TransactionReceipt> getReceiptsByTransactionsDateRange(Authentication auth, @PathVariable("account_id") long id, @RequestParam("start_date") Optional<String> startDate, @RequestParam("end_date") Optional<String> endDate) {
//
//  }

  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/receipts/{receipt_id}")
  @ResponseStatus(HttpStatus.OK)
  public TransactionReceipt getReceiptsByTransactionId(Authentication auth, @PathVariable("receipt_id") long receiptId) {
    try {
      TransactionReceipt receipt = transactionReceiptService.getById(receiptId);
      List<String> validUsernames = new ArrayList<>();
      validUsernames.add(receipt.getPersonalAccount().getPrimaryOwner().getUsername());
      if (receipt.getPersonalAccount().getSecondaryOwner() != null)
        validUsernames.add(receipt.getPersonalAccount().getSecondaryOwner().getUsername());

      if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) ||
          validUsernames.contains(auth.getName())) {
        return receipt;
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
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
