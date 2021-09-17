package com.ironhack.midterm.util.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class validInterestRateUtil {

  // ========================= Valid Interest Rate for Savings Account (>= 0, <= 0.5) =========================
  public static boolean isValidInterestRateForSavingsAccount(BigDecimal interestRate) {
    return interestRate.compareTo(BigDecimal.ZERO) >= 0 &&
        interestRate.compareTo(new BigDecimal("0.5")) <= 0;
  }


  // ============================== Valid Interest Rate for Credit Card (>= 0.1) ==============================
  public static boolean isValidInterestRateForCreditCard(BigDecimal interestRate) {
    return interestRate.compareTo(new BigDecimal("0.1")) >= 0;
  }


}
