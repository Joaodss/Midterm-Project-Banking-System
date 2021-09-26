package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.TransactionController;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.*;
import com.ironhack.midterm.dto.TransactionInternalDTO;
import com.ironhack.midterm.dto.TransactionLocalDTO;
import com.ironhack.midterm.dto.TransactionThirdPartyDTO;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.service.AccountManagerService;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.*;
import com.ironhack.midterm.service.user.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceNotFoundException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

import static com.ironhack.midterm.util.EnumsUtil.transactionTypeFromString;

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

  @Autowired
  private InterestTransactionService interestTransactionService;

  @Autowired
  private MaintenanceFeeTransactionService maintenanceFeeTransactionService;

  @Autowired
  private PenaltyFeeTransactionService penaltyFeeTransactionService;

  @Autowired
  private AccountManagerService accountManagerService;

  @Autowired
  private ThirdPartyService thirdPartyService;


  // ======================================== GET TRANSACTION Methods ========================================
  // -------------------- Account Specific Transactions [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions")
  @ResponseStatus(HttpStatus.OK)
  public List<Transaction> getTransactions(@PathVariable("account_id") long id) {
    try {
      accountService.getById(id);
      return transactionService.getAllByAccountId(id);
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions/{transaction_id}")
  @ResponseStatus(HttpStatus.OK)
  public Transaction getTransactionsById(@PathVariable("account_id") long accountId, @PathVariable("transaction_id") long transactionId) {
    try {
      Account account = accountService.getById(accountId);
      Transaction transaction = transactionService.getById(transactionId);


      if ((transaction.getBaseAccount() != null && transaction.getBaseAccount().getId() != accountId)
          && transaction.getTargetAccount().getId() != accountId)
        throw new IllegalArgumentException("Transaction does not exist in defined account.");

      return transaction;

    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction does not exist in defined account.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== GET Receipt Methods ========================================
  // -------------------- User Specific Transactions by Id [ADMIN / Specific USER] --------------------
  @GetMapping("/{account_id}/transactions/{transaction_id}/receipt")
  @ResponseStatus(HttpStatus.OK)
  public Receipt getReceiptsByTransactionId(@PathVariable("account_id") long accountId, @PathVariable("transaction_id") long transactionId) {
    try {
      Account account = accountService.getById(accountId);
      Transaction transaction = transactionService.getById(transactionId);

      if ((transaction.getBaseAccount() != null && transaction.getBaseAccount().getId() != accountId)
          && transaction.getTargetAccount().getId() != accountId)
        throw new IllegalArgumentException("Transaction does not exist in defined account.");

      Receipt receipt = null;
      for (Receipt tr : transaction.getReceipts()) {
        if (tr.getPersonalAccount().getId() == accountId) receipt = tr;
      }

      if (receipt != null) return receipt;

      throw new InstanceNotFoundException("Receipt not found.");

    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== POST ACCOUNT Methods ========================================
  // -------------------- Add Account Specific Local Transaction [ADMIN / Specific USER] --------------------
  @PostMapping("/{account_id}/transactions/new_local_transaction")
  @ResponseStatus(HttpStatus.CREATED)
  public void createLocalTransaction(@PathVariable("account_id") long id, @RequestBody @Valid TransactionLocalDTO localTransaction) {
    try {
      Account account = accountService.getById(id);
      Transaction transaction = localTransactionService.newTransaction(id, localTransaction);
      localTransactionService.validateLocalTransaction(transaction);
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Id not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction parameters.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Add Account Specific Third Party Transaction [ADMIN / Specific USER] --------------------
  @PostMapping("/transactions/new_third_party_transaction")
  @ResponseStatus(HttpStatus.CREATED)
  public void createThirdPartyTransaction(@RequestHeader(value = "hashedKey") String hashedKey, @RequestBody @Valid TransactionThirdPartyDTO thirdPartyTransaction) {
    try {
      if (thirdPartyService.hasHashedKey(hashedKey)) {
        Account account = accountService.getById(thirdPartyTransaction.getTargetAccountId());
        Transaction transaction = thirdPartyTransactionService.newTransaction(thirdPartyTransaction);
        thirdPartyTransactionService.validateThirdPartyTransaction(transaction);
      } else {
        throw new IllegalArgumentException("Invalid hashed key.");
      }
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Id not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction parameters.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @PostMapping("/{account_id}/transactions/new_internal_transaction")
  @ResponseStatus(HttpStatus.CREATED)
  public void createInternalTransaction(@PathVariable("account_id") long id, @RequestBody @Valid TransactionInternalDTO internalTransactions) {
    try {
      accountService.getById(id);
      TransactionType transactionType = transactionTypeFromString(internalTransactions.getTransactionType());
      if (transactionType == TransactionType.INTEREST) {
        Transaction transaction = interestTransactionService.newTransaction(id);
        interestTransactionService.validateInterestTransaction(transaction);
      } else if (transactionType == TransactionType.MAINTENANCE_FEE) {
        Transaction transaction = maintenanceFeeTransactionService.newTransaction(id);
        maintenanceFeeTransactionService.validateMaintenanceFeeTransaction(transaction);
      } else if (transactionType == TransactionType.PENALTY_FEE) {
        Transaction transaction = penaltyFeeTransactionService.newTransaction(id);
        penaltyFeeTransactionService.validatePenaltyFeeTransaction(transaction);
      }
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account Id not found.");
    } catch (IllegalArgumentException e2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction parameters.");
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
