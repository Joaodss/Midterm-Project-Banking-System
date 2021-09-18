package com.ironhack.midterm.util;

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

import static com.ironhack.midterm.util.MoneyUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MoneyUtilTest {

  // ======================================== new Money ========================================
  @ParameterizedTest
  @ValueSource(strings = {"0", "3.333", "235723572.5721571"})
  @Order(1)
  void testNewMoney_validStringNumber(String value) {
    assertEquals(new Money(new BigDecimal(value)), newMoney(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "ah3haefhb", "157135,13532"})
  @Order(1)
  void testNewMoney_invalidStringNumber(String value) {
    assertThrows(NumberFormatException.class, () -> newMoney(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"EUR", "USD", "CHF"})
  @Order(1)
  void testNewMoney_validStringCurrency(String currency) {
    assertEquals(new Money(new BigDecimal("100"), Currency.getInstance(currency)), newMoney("100", currency));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "euro", "EURO", "hst"})
  @Order(1)
  void testNewMoney_invalidStringCurrency(String currency) {
    assertThrows(IllegalArgumentException.class, () -> newMoney("100", currency));
  }

  @Test
  @Order(1)
  void testNewMoney_toString() {
    var moneyResult = newMoney("42");
    var moneyResult2 = newMoney("45", "USD");
    assertEquals("€ 42.00", moneyResult.toString());
    assertEquals("42.00", moneyResult.getAmount().toString());
    assertEquals("$ 45.00", moneyResult2.toString());
    assertEquals("45.00", moneyResult2.getAmount().toString());
  }


  // ======================================== new BigDecimal ========================================
  @ParameterizedTest
  @Order(2)
  @ValueSource(strings = {"0", "3.333", "235723572.5721571"})
  void testNewBD_validStringNumber(String value) {
    assertEquals(new BigDecimal(value), newBD(value));
    assertEquals(new BigDecimal(value).setScale(3, RoundingMode.HALF_EVEN), newBD(value, 3));
  }

  @ParameterizedTest
  @Order(2)
  @ValueSource(strings = {"", "  ", "ah3haefhb", "157135,13532"})
  void testNewBD_invalidStringNumber(String value) {
    assertThrows(NumberFormatException.class, () -> newBD(value));
    assertThrows(NumberFormatException.class, () -> newBD(value, 2));
  }

  @Test
  @Order(2)
  void testNewBD_toString() {
    assertEquals("42", MoneyUtil.newBD("42").toString());
    assertEquals("42.00", MoneyUtil.newBD("42", 2).toString());
    assertEquals("42", MoneyUtil.newBD("42", 0).toString());
  }


  // ======================================== Is Same Currency ========================================
  @Test
  @Order(3)
  void testIsSameCurrency_sameCurrency_true() {
    var m1 = newMoney("100", "EUR");
    var m2 = newMoney("350.5", "EUR");
    assertTrue(isSameCurrency(m1, m2));
    assertTrue(isSameCurrency(m1.getCurrency(), m2));
  }

  @Test
  @Order(3)
  void testIsSameCurrency_differentCurrency_false() {
    var m1 = newMoney("100", "EUR");
    var m2 = newMoney("350.5", "USD");
    assertFalse(isSameCurrency(m1, m2));
    assertFalse(isSameCurrency(m1.getCurrency(), m2));
  }

  // ======================================== Is Same Currency ========================================
  @Test
  @Order(4)
  void testConvertCurrency_multipleConversions_differentCurrency_convertValues() { // slow
    var initialMoney1 = newMoney("100", "USD");
    var initialMoney2 = newMoney("200", "CHF");
    var money1 = convertCurrency(Currency.getInstance("EUR"), initialMoney1);
    var money2 = convertCurrency(newMoney("1000", "USD"), initialMoney2);
    assertNotEquals(initialMoney1, money1);
    assertEquals("EUR", money1.getCurrency().getCurrencyCode());
    assertNotEquals(initialMoney2, money2);
    assertEquals("USD", money2.getCurrency().getCurrencyCode());
  }

  @Test
  @Order(4)
  void testConvertCurrency_multipleConversions_sameCurrency_convertValues() { // less slow
    var initialMoney1 = newMoney("100", "USD");
    var initialMoney2 = newMoney("200", "CHF");
    var money1 = convertCurrency(Currency.getInstance("EUR"), initialMoney1);
    var money2 = convertCurrency(newMoney("1000", "EUR"), initialMoney2);
    assertNotEquals(initialMoney1, money1);
    assertEquals("EUR", money1.getCurrency().getCurrencyCode());
    assertNotEquals(initialMoney2, money2);
    assertEquals("EUR", money2.getCurrency().getCurrencyCode());
  }

  @Test
  @Order(4)
  void testConvertCurrency_sameCurrency_returnInitialValue() {
    var initialMoney1 = newMoney("10");
    var initialMoney2 = newMoney("20", "USD");
    var money1 = convertCurrency(newMoney("1"), initialMoney1);
    var money2 = convertCurrency(Currency.getInstance("USD"), initialMoney2);
    assertEquals(money1, initialMoney1);
    assertEquals(money2, initialMoney2);
  }


}
