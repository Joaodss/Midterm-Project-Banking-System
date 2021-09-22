package com.ironhack.midterm.util;

import com.ironhack.midterm.enums.TransactionPurpose;

public class TransactionPurposeUtil {

  // ============================== Valid Status from String (exists) ==============================
  public static boolean isValidTransactionPurposeFromString(String stringTransactionPurpose) {
    for (TransactionPurpose tp : TransactionPurpose.values()) {
      if (tp.name().equalsIgnoreCase(stringTransactionPurpose))
        return true;
    }
    return false;
  }

  // =================================== Get Status from String ===================================
  public static TransactionPurpose transactionPurposeFromString(String stringTransactionPurpose) {
    for (TransactionPurpose tp : TransactionPurpose.values()) {
      if (tp.name().equalsIgnoreCase(stringTransactionPurpose))
        return tp;
    }
    throw new IllegalArgumentException("TransactionPurpose " + stringTransactionPurpose + " does not exist.");
  }

}
