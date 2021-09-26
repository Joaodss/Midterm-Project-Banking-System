package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private AccountService accountService;


  public Receipt getReceiptByTransactionId(long accountId, long transactionId) {
    if (!accountService.hasAccount(accountId)) throw new EntityNotFoundException("Account not found.");

    var receipt = receiptRepository.findByTransactionIdJoined(accountId, transactionId);
    if (receipt.isPresent()) return receipt.get();
    throw new IllegalArgumentException("Receipt not found. Check if transaction belongs to defined account and if transaction was processed.");
  }

}
