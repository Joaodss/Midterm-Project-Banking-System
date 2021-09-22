package com.ironhack.midterm.util;

import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.TransactionPurpose;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumsUtil {

  // ============================== Valid Status from String (exists) ==============================
  public static boolean isValidStatusFromString(String stringStatus) {
    for (AccountStatus s : AccountStatus.values()) {
      if (s.name().equalsIgnoreCase(stringStatus))
        return true;
    }
    return false;
  }

  // =================================== Get Status from String ===================================
  public static AccountStatus statusFromString(String stringStatus) {
    for (AccountStatus s : AccountStatus.values()) {
      if (s.name().equalsIgnoreCase(stringStatus))
        return s;
    }
    throw new IllegalArgumentException("Status " + stringStatus + " does not exist.");
  }


  // ============================== Valid TransactionPurpose from String (exists) ==============================
  public static boolean isValidTransactionPurposeFromString(String stringTransactionPurpose) {
    for (TransactionPurpose tp : TransactionPurpose.values()) {
      if (tp.name().equalsIgnoreCase(stringTransactionPurpose))
        return true;
    }
    return false;
  }

  // =================================== Get TransactionPurpose from String ===================================
  public static TransactionPurpose transactionPurposeFromString(String stringTransactionPurpose) {
    for (TransactionPurpose tp : TransactionPurpose.values()) {
      if (tp.name().equalsIgnoreCase(stringTransactionPurpose))
        return tp;
    }
    throw new IllegalArgumentException("TransactionPurpose " + stringTransactionPurpose + " does not exist.");
  }

}
