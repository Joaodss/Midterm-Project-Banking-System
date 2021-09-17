package com.ironhack.midterm.util.money;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Currency;

import static com.ironhack.midterm.util.money.MoneyConverterUtil.convertCurrency;
import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MoneyConverterUtilTest {

  // ======================================== Is Same Currency ========================================
  @Order(1)
  @Test
  void testIsSameCurrency_sameCurrency_true() {
    var m1 = newMoney("100", "EUR");
    var m2 = newMoney("350.5", "EUR");
    assertTrue(MoneyConverterUtil.isSameCurrency(m1, m2));
    assertTrue(MoneyConverterUtil.isSameCurrency(m1.getCurrency(), m2));
  }

  @Order(1)
  @Test
  void testIsSameCurrency_differentCurrency_false() {
    var m1 = newMoney("100", "EUR");
    var m2 = newMoney("350.5", "USD");
    assertFalse(MoneyConverterUtil.isSameCurrency(m1, m2));
    assertFalse(MoneyConverterUtil.isSameCurrency(m1.getCurrency(), m2));
  }

  // ======================================== Is Same Currency ========================================

  @Order(2)
  @Test
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

  @Order(2)
  @Test
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

  @Order(2)
  @Test
  void testConvertCurrency_sameCurrency_returnInitialValue() {
    var initialMoney = newMoney("10");
    var money = convertCurrency(Currency.getInstance("EUR"), initialMoney);
    assertEquals(money, initialMoney);
  }


}

