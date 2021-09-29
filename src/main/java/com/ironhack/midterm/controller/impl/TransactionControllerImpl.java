package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.TransactionController;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dto.TransactionLocalDTO;
import com.ironhack.midterm.dto.TransactionThirdPartyDTO;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.*;
import com.ironhack.midterm.service.user.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class TransactionControllerImpl implements TransactionController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private LocalTransactionService localTransactionService;

  @Autowired
  private ThirdPartyTransactionService thirdPartyTransactionService;

  @Autowired
  private InterestTransactionService interestTransactionService;

  @Autowired
  private MaintenanceFeeTransactionService maintenanceFeeTransactionService;

  @Autowired
  private PenaltyFeeTransactionService penaltyFeeTransactionService;

  @Autowired
  private ThirdPartyService thirdPartyService;

  @Autowired
  private ReceiptService receiptService;


  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions")
  @ResponseStatus(HttpStatus.OK)
  public List<Transaction> getTransactions(@PathVariable("account_id") long id) {
    try {
      accountService.updateBalance(accountService.getById(id));
      return transactionService.getAllByAccountId(id);
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions/{transaction_id}")
  @ResponseStatus(HttpStatus.OK)
  public Transaction getTransactionsById(@PathVariable("account_id") long accountId, @PathVariable("transaction_id") long transactionId) {
    try {
      accountService.updateBalance(accountService.getById(accountId));
      return transactionService.getById(accountId, transactionId);
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e1.getMessage());
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


  // ======================================== GET Receipt Methods ========================================
  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions/{transaction_id}/receipt")
  @ResponseStatus(HttpStatus.OK)
  public Receipt getReceiptsByTransactionId(@PathVariable("account_id") long accountId, @PathVariable("transaction_id") long transactionId) {
    try {
      accountService.updateBalance(accountService.getById(accountId));
      return receiptService.getReceiptByTransactionId(accountId, transactionId);
    } catch (IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e1.getMessage());
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


  // ======================================== POST ACCOUNT Methods ========================================
  // -------------------- Add Account Specific Local Transaction [ADMIN / Specific USER] --------------------
  @PostMapping("/{account_id}/transactions/new_local_transaction")
  @ResponseStatus(HttpStatus.CREATED)
  public void createLocalTransaction(@PathVariable("account_id") long id, @RequestBody @Valid TransactionLocalDTO localTransaction) {
    try {
      localTransactionService.newTransaction(id, localTransaction);
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e1.getMessage());
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Add Account Specific Third Party Transaction [ADMIN / Specific USER] --------------------
  @PostMapping("/transactions/new_third_party_transaction")
  @ResponseStatus(HttpStatus.CREATED)
  public void createThirdPartyTransaction(@RequestHeader(value = "hashedKey") String hashedKey, @RequestBody @Valid TransactionThirdPartyDTO thirdPartyTransaction) {
    try {
      thirdPartyTransactionService.newTransaction(hashedKey, thirdPartyTransaction);
    } catch (EntityNotFoundException | IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e1.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

}
