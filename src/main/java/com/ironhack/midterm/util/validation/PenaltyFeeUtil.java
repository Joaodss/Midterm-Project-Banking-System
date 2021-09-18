package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PenaltyFeeUtil {

  // ======================================== Valid Penalty Fee (>= 0) ========================================
  public static boolean isValidPenaltyFee(Money penaltyFee) {
    return penaltyFee.getAmount().compareTo(BigDecimal.ZERO) >= 0;
  }

  public static boolean isValidPenaltyFee(BigDecimal penaltyFee) {
    return penaltyFee.compareTo(BigDecimal.ZERO) >= 0;
  }


}
