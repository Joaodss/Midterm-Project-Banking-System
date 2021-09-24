package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
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
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== GET Methods ========================================
  public Receipt getById(long transactionId) throws InstanceNotFoundException {
    var transaction = receiptRepository.findByIdJoined(transactionId);
    if (transaction.isPresent()) return transaction.get();
    throw new InstanceNotFoundException();
  }

}
