package com.ironhack.midterm.util.money;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newBD;
import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newMoney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MoneyInitializerUtilTest {

  // ======================================== new Money ========================================
  @Order(1)
  @ParameterizedTest
  @ValueSource(strings = {"0", "3.333", "235723572.5721571"})
  void testNewMoney_validStringNumber(String value) {
    assertEquals(new Money(new BigDecimal(value)), newMoney(value));
  }

  @Order(1)
  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "ah3haefhb", "157135,13532"})
  void testNewMoney_invalidStringNumber(String value) {
    assertThrows(NumberFormatException.class, () -> newMoney(value));
  }

  @Order(1)
  @ParameterizedTest
  @ValueSource(strings = {"EUR", "USD", "CHF"})
  void testNewMoney_validStringCurrency(String currency) {
    assertEquals(new Money(new BigDecimal("100"), Currency.getInstance(currency)), newMoney("100", currency));
  }

  @Order(1)
  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "euro", "EURO", "hst"})
  void testNewMoney_invalidStringCurrency(String currency) {
    assertThrows(IllegalArgumentException.class, () -> newMoney("100", currency));
  }

  @Order(1)
  @Test
  void testNewMoney_toString() {
    var moneyResult = newMoney("42");
    var moneyResult2 = newMoney("45", "USD");
    assertEquals("€ 42.00", moneyResult.toString());
    assertEquals("42.00", moneyResult.getAmount().toString());
    assertEquals("$ 45.00", moneyResult2.toString());
    assertEquals("45.00", moneyResult2.getAmount().toString());
  }


  // ======================================== new BigDecimal ========================================
  @Order(2)
  @ParameterizedTest
  @ValueSource(strings = {"0", "3.333", "235723572.5721571"})
  void testNewBD_validStringNumber(String value) {
    assertEquals(new BigDecimal(value), newBD(value));
    assertEquals(new BigDecimal(value).setScale(3, RoundingMode.HALF_EVEN), newBD(value, 3));
  }

  @Order(2)
  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "ah3haefhb", "157135,13532"})
  void testNewBD_invalidStringNumber(String value) {
    assertThrows(NumberFormatException.class, () -> {
      newBD(value);
      newBD(value, 2);
    });
  }

  @Order(2)
  @Test
  void testNewBD_toString() {
    assertEquals("42", MoneyInitializerUtil.newBD("42").toString());
    assertEquals("42.00", MoneyInitializerUtil.newBD("42", 2).toString());
    assertEquals("42", MoneyInitializerUtil.newBD("42", 0).toString());
  }


}
