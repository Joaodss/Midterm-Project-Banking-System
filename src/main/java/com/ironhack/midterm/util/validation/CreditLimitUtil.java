package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditLimitUtil {

  // ========================= Valid Credit Limit for Credit Card (>= 0, <= 100000) =========================
  public static boolean isValidCreditLimitForCreditCard(Money creditLimit) {
    return creditLimit.getAmount().compareTo(BigDecimal.ZERO) >= 0 &&
        creditLimit.getAmount().compareTo(new BigDecimal("100000")) <= 0;
  }

  public static boolean isValidCreditLimitForCreditCard(BigDecimal creditLimit) {
    return creditLimit.compareTo(BigDecimal.ZERO) >= 0 &&
        creditLimit.compareTo(new BigDecimal("100000")) <= 0;
  }


}
