package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== GET Methods ========================================
  public List<Transaction> getAllByAccountId(long accountId) {
    if (accountService.hasAccount(accountId))
      return transactionRepository.findAllByAccountIdJoined(accountId);

    throw new EntityNotFoundException("Account not found.");
  }


  public Transaction getById(long accountId, long transactionId) {
    if (!accountService.hasAccount(accountId)) throw new EntityNotFoundException("Account not found.");

    var transaction = transactionRepository.findByIdJoined(transactionId);
    if (transaction.isPresent()) {
      if ((transaction.get().getBaseAccount() == null || transaction.get().getBaseAccount().getId() != accountId) &&
          transaction.get().getTargetAccount().getId() != accountId)
        throw new IllegalArgumentException("Transaction does not exist in defined account.");

      return transaction.get();
    }
    throw new EntityNotFoundException("Transaction not found.");
  }

}
