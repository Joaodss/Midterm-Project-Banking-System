package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import com.ironhack.midterm.repository.transaction.TransactionReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.TransactionReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionReceiptServiceImpl implements TransactionReceiptService {

  @Autowired
  private TransactionReceiptRepository transactionReceiptRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== GET Methods ========================================
  public List<TransactionReceipt> getAllByAccountId(long accountId) throws InstanceNotFoundException {
    accountService.getById(accountId); // to check if account exists
    return transactionReceiptRepository.findAllByAccountIdJoined(accountId);
  }

  @Override
  public List<TransactionReceipt> getByAccountIdByDateRange(long AccountId, Optional<String> startDate, Optional<String> endDate) {
    return null;
  }


  public TransactionReceipt getById(long transactionId) throws InstanceNotFoundException {
    var transaction = transactionReceiptRepository.findByIdJoined(transactionId);
    if (transaction.isPresent()) return transaction.get();
    throw new InstanceNotFoundException();
  }

}
