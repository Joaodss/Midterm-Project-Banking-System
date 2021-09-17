package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class validMinimumBalance {

  // =================================== Valid Minimum Balance (>= 100) ===================================
  public static boolean isValidMinimumBalance(Money minimumBalance) {
    return minimumBalance.getAmount().compareTo(BigDecimal.ZERO) >= 0;
  }

  public static boolean isValidMinimumBalance(BigDecimal minimumBalance) {
    return minimumBalance.compareTo(BigDecimal.ZERO) >= 0;
  }


  // ========================= Valid Minimum Balance for Savings Account (>= 100) =========================
  public static boolean isValidMinimumBalanceForSavingsAccount(Money minimumBalance) {
    return minimumBalance.getAmount().compareTo(new BigDecimal("100")) >= 0;
  }

  public static boolean isValidMinimumBalanceForSavingsAccount(BigDecimal minimumBalance) {
    return minimumBalance.compareTo(new BigDecimal("100")) >= 0;
  }


}
