package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static com.ironhack.midterm.util.validation.BalanceUtil.isValidBalance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BalanceUtilTest {

  // ======================================== Is Valid Balance ========================================
  @ParameterizedTest
  @ValueSource(strings = {"0", "0.01", "1", "35723.677"})
  @Order(1)
  void testIsValidBalance_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidBalance(value));
    assertTrue(isValidBalance(new Money(value)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "-1", "-35723.677"})
  @Order(1)
  void testIsValidBalance_invalidValues_false(String values) {
    BigDecimal value = new BigDecimal(values);
    System.out.println(value);
    assertFalse(isValidBalance(value));
    assertFalse(isValidBalance(new Money(value)));
  }


  // =================================== Is Valid Balance Transfer ===================================
  @ParameterizedTest
  @ValueSource(strings = {"10", "100", "1000", "1357.14"})
  @Order(2)
  void testIsValidTransfer_validTransferValue_true(String value) {
    Money balance = new Money(new BigDecimal("1357.14"));
    Money transferQuantity = new Money(new BigDecimal(value));
    assertTrue(BalanceUtil.isValidTransfer(balance, transferQuantity));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1357.15", "1357.8", "2000"})
  @Order(2)
  void testIsValidTransfer_invalidTransferValue_false(String value) {
    Money balance = new Money(new BigDecimal("1357.14"));
    Money transferQuantity = new Money(new BigDecimal(value));
    assertFalse(BalanceUtil.isValidTransfer(balance, transferQuantity));
  }


  // ============================== Is Valid Balance Transfer (with convert) ==============================
  @Test
  @Order(3)
  void testIsValidTransfer_validDifferentCurrency_true() {
    Money balance = new Money(new BigDecimal("10000"));
    Money transferQuantity = new Money(new BigDecimal("0.81"), Currency.getInstance("USD"));
    assertTrue(BalanceUtil.isValidTransfer(balance, transferQuantity));
  }

  @Test
  @Order(3)
  void testIsValidTransfer_invalidDifferentCurrency_false() {
    Money balance = new Money(new BigDecimal("0.5"));
    Money transferQuantity = new Money(new BigDecimal("900000"), Currency.getInstance("USD"));
    assertFalse(BalanceUtil.isValidTransfer(balance, transferQuantity));
  }


}
