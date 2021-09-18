package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;
import static com.ironhack.midterm.util.MoneyUtil.isSameCurrency;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceUtil {

  // ======================================== Valid Balance (>= 0) ========================================
  public static boolean isValidBalance(Money balance) {
    return balance.getAmount().compareTo(BigDecimal.ZERO) >= 0;
  }

  public static boolean isValidBalance(BigDecimal balance) {
    return balance.compareTo(BigDecimal.ZERO) >= 0;
  }


  // ============================== Valid Balance Transfer (result >= 0) ==============================
  public static boolean isValidTransfer(Money balance, Money quantity) {
    Money newQuantity = quantity;
    // Check if is same currency. If not convert quantity.
    if (!isSameCurrency(balance, quantity))
      newQuantity = convertCurrency(balance, quantity);
    return balance.getAmount().compareTo(newQuantity.getAmount()) >= 0;
  }


}
