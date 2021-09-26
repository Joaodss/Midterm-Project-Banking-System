package com.ironhack.midterm.util;

import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.enums.TransactionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumsUtil {

  // =================================== Get Status from String ===================================
  public static AccountStatus accountStatusFromString(String stringStatus) {
    for (AccountStatus s : AccountStatus.values()) {
      if (s.name().equalsIgnoreCase(stringStatus))
        return s;
    }
    throw new IllegalArgumentException("Status " + stringStatus + " does not exist.");
  }

  // =================================== Get TransactionPurpose from String ===================================
  public static TransactionPurpose transactionPurposeFromString(String stringTransactionPurpose) {
    for (TransactionPurpose tp : TransactionPurpose.values()) {
      if (tp.name().equalsIgnoreCase(stringTransactionPurpose))
        return tp;
    }
    throw new IllegalArgumentException("TransactionPurpose " + stringTransactionPurpose + " does not exist.");
  }

  // =================================== Get TransactionType from String ===================================
  public static TransactionType transactionTypeFromString(String stringTransactionType) {
    for (TransactionType tt : TransactionType.values()) {
      if (tt.name().equalsIgnoreCase(stringTransactionType))
        return tt;
    }
    throw new IllegalArgumentException("TransactionPurpose " + stringTransactionType + " does not exist.");
  }


}
