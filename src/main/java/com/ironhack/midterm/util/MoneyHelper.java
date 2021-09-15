package com.ironhack.midterm.util;

import com.ironhack.midterm.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyHelper {


  public static Money newMoney(String value) {
    return new Money(new BigDecimal(value));
  }

  public static BigDecimal newBD(String value) {
    return new BigDecimal(value);
  }

  public static BigDecimal newBD(String value, int scale) {
    return new BigDecimal(value).setScale(scale, RoundingMode.HALF_EVEN);
  }


}
