package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class validMonthlyMaintenanceFee {

  // ============================== Valid Monthly Maintenance Fee (>= 0) ==============================
  public static boolean isValidMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
    return monthlyMaintenanceFee.getAmount().compareTo(BigDecimal.ZERO) >= 0;
  }

  public static boolean isValidMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
    return monthlyMaintenanceFee.compareTo(BigDecimal.ZERO) >= 0;
  }


}
