package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.TransactionController;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import com.ironhack.midterm.dto.LocalTransactionDTO;
import com.ironhack.midterm.dto.ThirdPartyTransactionDTO;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.LocalTransactionService;
import com.ironhack.midterm.service.transaction.ThirdPartyTransactionService;
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
import java.util.List;

import static com.ironhack.midterm.util.AuthorisationUtil.isAccountOwnerOrAdmin;

@RestController
@RequestMapping("/api/accounts")
public class TransactionControllerImpl implements TransactionController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private TransactionReceiptService transactionReceiptService;

  @Autowired
  private LocalTransactionService localTransactionService;

  @Autowired
  private ThirdPartyTransactionService thirdPartyTransactionService;


  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions")
  @ResponseStatus(HttpStatus.OK)
  public List<Transaction> getTransactions(Authentication auth, @PathVariable("account_id") long id) {
    try {
      Account account = accountService.getById(id);
      if (isAccountOwnerOrAdmin(auth, account)) {
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
  @GetMapping("/{account_id}/transactions/{transaction_id}")
  @ResponseStatus(HttpStatus.OK)
  public Transaction getTransactionsById(Authentication auth, @PathVariable("account_id") long accountId, @PathVariable("transaction_id") long transactionId) {
    try {
      Account account = accountService.getById(accountId);
      Transaction transaction = transactionService.getById(transactionId);

      if (transaction.getBaseAccount().getId() != accountId && transaction.getBaseAccount().getId() != accountId)
        throw new IllegalArgumentException("Transaction does not exist in defined account.");

      if (isAccountOwnerOrAdmin(auth, account)) {
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


  // ======================================== GET Receipt Methods ========================================
  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions/{transaction_id}/receipt")
  @ResponseStatus(HttpStatus.OK)
  public TransactionReceipt getReceiptsByTransactionId(Authentication auth, @PathVariable("account_id") long accountId, @PathVariable("transaction_id") long transactionId) {
    try {
      Account account = accountService.getById(accountId);
      Transaction transaction = transactionService.getById(transactionId);

      if (transaction.getBaseAccount().getId() != accountId && transaction.getBaseAccount().getId() != accountId)
        throw new IllegalArgumentException("Transaction does not exist in defined account.");

      if (isAccountOwnerOrAdmin(auth, account)) {
        TransactionReceipt receipt = null;
        for (TransactionReceipt tr : transaction.getReceipts()) {
          if (tr.getPersonalAccount().getId() == accountId) receipt = tr;
        }
        if (receipt != null) {
          return receipt;
        }
        throw new InstanceNotFoundException("Receipt not found.");
      }
      throw new LoginException("Invalid user logg in.");
    } catch (LoginException e1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user logg in.");
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== POST ACCOUNT Methods ========================================
  // -------------------- Add Account Specific Local Transaction [ADMIN / Specific USER] --------------------
  @PostMapping("/{account_id}/transactions/new")
  @ResponseStatus(HttpStatus.CREATED)
  public void createLocalTransaction(Authentication auth, @PathVariable("account_id") long id, @RequestBody @Valid LocalTransactionDTO localTransaction) {
    try {
      Account account = accountService.getById(id);
      if (isAccountOwnerOrAdmin(auth, account)) {
        localTransactionService.newTransaction(id, localTransaction);
      }
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Id not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction parameters.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Add Account Specific Third Party Transaction [ADMIN / Specific USER] --------------------
//  @PostMapping()
//  @ResponseStatus(HttpStatus.CREATED)
//  public void createThirdPartyTransaction(Authentication auth, @RequestBody @Valid ThirdPartyTransactionDTO localTransaction) {
//
//
//  }

  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
