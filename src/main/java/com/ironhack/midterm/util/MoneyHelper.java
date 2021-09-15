package com.ironhack.midterm.util;

import com.ironhack.midterm.model.Money;

import java.math.BigDecimal;

public class MoneyHelper {


  public static Money newMoney(String value) {
    return new Money(new BigDecimal(value));
  }

  public static BigDecimal newBD(String value) {
    return new BigDecimal(value);
  }


}
