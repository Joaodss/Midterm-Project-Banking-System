package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== GET Methods ========================================
  public List<Transaction> getAllByAccountId(long accountId) throws InstanceNotFoundException {
    accountService.getById(accountId); // to check if account exists
    return transactionRepository.findAllByAccountIdJoined(accountId);
  }


  public Transaction getById(long transactionId) throws InstanceNotFoundException {
    var transaction = transactionRepository.findByIdJoined(transactionId);
    if (transaction.isPresent()) return transaction.get();
    throw new InstanceNotFoundException();
  }

}
