package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Receipt;

public interface ReceiptService {

  // ======================================== get Methods ========================================
  Receipt getReceiptByTransactionId(long accountId, long transactionId);

}
