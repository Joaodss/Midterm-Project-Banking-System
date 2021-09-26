package com.ironhack.midterm.util;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static com.ironhack.midterm.util.MoneyUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MoneyUtilTest {

  // =================================== Simplify new Money ===================================
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


  // =================================== Is Same Currency ===================================
  @Test
  @Order(2)
  void testIsSameCurrency_sameCurrency_true() {
    var m1 = newMoney("100", "EUR");
    var m2 = newMoney("350.5", "EUR");
    assertTrue(isSameCurrency(m1, m2));
    assertTrue(isSameCurrency(m1.getCurrency(), m2));
  }

  @Test
  @Order(2)
  void testIsSameCurrency_differentCurrency_false() {
    var m1 = newMoney("100", "EUR");
    var m2 = newMoney("350.5", "USD");
    assertFalse(isSameCurrency(m1, m2));
    assertFalse(isSameCurrency(m1.getCurrency(), m2));
  }

  // =================================== Convert Currency ===================================
  @Test
  @Order(3)
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
  @Order(3)
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
  @Order(3)
  void testConvertCurrency_sameCurrency_returnInitialValue() {
    var initialMoney1 = newMoney("10");
    var initialMoney2 = newMoney("20", "USD");
    var money1 = convertCurrency(newMoney("1"), initialMoney1);
    var money2 = convertCurrency(Currency.getInstance("USD"), initialMoney2);
    assertEquals(money1, initialMoney1);
    assertEquals(money2, initialMoney2);
  }


  // =================================== Negative Balance ===================================
  @Test
  @Order(4)
  void testNegativeCurrency_sameCurrency_returnNegativeAmount() {
    Money money = newMoney("100");
    assertEquals(new Money(new BigDecimal("-100"), Currency.getInstance("EUR")), negativeMoney(money));
  }

  // =================================== Compare Balance ===================================
  @Test
  @Order(5)
  void testCompareCurrency_equalCurrency_return0() {
    Money money1 = newMoney("100");
    Money money2 = newMoney("100");
    assertEquals(0, compareMoney(money1, money2));
  }

  @Test
  @Order(5)
  void testCompareCurrency_greaterThanCurrency_return1() {
    Money money1 = newMoney("200");
    Money money2 = newMoney("100");
    assertEquals(1, compareMoney(money1, money2));
  }

  @Test
  @Order(5)
  void testCompareCurrency_greaterThanCurrency_returnNegative1() {
    Money money1 = newMoney("100");
    Money money2 = newMoney("200");
    assertEquals(-1, compareMoney(money1, money2));
  }

  // =================================== Calculate Balance (Subtraction)===================================
  @Test
  @Order(6)
  void testSubtractMoney_subtract_returnCalculusAndBaseCurrency() {
    Money money1 = newMoney("500");
    Money money2 = newMoney("200");
    Money result = subtractMoney(money1, money2);
    assertEquals(new BigDecimal("300.00"), result.getAmount());
    assertEquals(Currency.getInstance("EUR"), result.getCurrency());
  }

  // =================================== Calculate Balance (Addition)===================================
  @Test
  @Order(7)
  void testAddMoney_add_returnCalculusAndBaseCurrency() {
    Money money1 = newMoney("500");
    Money money2 = newMoney("200");
    Money result = addMoney(money1, money2);
    assertEquals(new BigDecimal("700.00"), result.getAmount());
    assertEquals(Currency.getInstance("EUR"), result.getCurrency());
  }
}
