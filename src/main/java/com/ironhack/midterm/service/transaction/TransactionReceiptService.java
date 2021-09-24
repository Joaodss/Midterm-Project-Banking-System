package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Receipt;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

public interface TransactionReceiptService {

  // ======================================== GET Methods ========================================

  Receipt getById(long transactionReceiptId) throws InstanceNotFoundException;

}
