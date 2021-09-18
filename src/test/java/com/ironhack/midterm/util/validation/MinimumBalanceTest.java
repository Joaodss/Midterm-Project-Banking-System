package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.ironhack.midterm.util.validation.MinimumBalance.isValidMinimumBalance;
import static com.ironhack.midterm.util.validation.MinimumBalance.isValidMinimumBalanceForSavingsAccount;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MinimumBalanceTest {

  // ======================================== Is Valid Minimum Balance ========================================
  @ParameterizedTest
  @ValueSource(strings = {"0", "0.01", "1", "35723.677"})
  @Order(1)
  void testIsValidMinimumBalance_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidMinimumBalance(value));
    assertTrue(isValidMinimumBalance(new Money(value)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "-1", "-35723.677"})
  @Order(1)
  void testIsValidMinimumBalance_invalidValues_false(String values) {
    BigDecimal value = new BigDecimal(values);
    System.out.println(value);
    assertFalse(isValidMinimumBalance(value));
    assertFalse(isValidMinimumBalance(new Money(value)));
  }


  // ========================= Is Valid Minimum Balance for Savings Account (>= 100) =========================
  @ParameterizedTest
  @ValueSource(strings = {"100", "100.01", "3573.677"})
  @Order(2)
  void testIsValidMinimumBalanceForSavingsAccount_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidMinimumBalanceForSavingsAccount(value));
    assertTrue(isValidMinimumBalanceForSavingsAccount(new Money(value)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.4", "0", "35", "99.991"})
  @Order(2)
  void testIsValidMinimumBalanceForSavingsAccount_invalidValues_false(String values) {
    BigDecimal value = new BigDecimal(values);
    System.out.println(value);
    assertFalse(isValidMinimumBalanceForSavingsAccount(value));
    assertFalse(isValidMinimumBalanceForSavingsAccount(new Money(value)));
  }


}

