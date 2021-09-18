package com.ironhack.midterm.util.validation;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.ironhack.midterm.util.validation.InterestRateUtil.isValidInterestRateForCreditCard;
import static com.ironhack.midterm.util.validation.InterestRateUtil.isValidInterestRateForSavingsAccount;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InterestRateUtilTest {

  // ========================= Is Valid Interest Rate for Savings Account (>= 0, <= 0.5) =========================
  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00001", "0.2638", "0.5"})
  @Order(1)
  void testIsValidInterestRateForSavingsAccount_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidInterestRateForSavingsAccount(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-0.0001", "0.500001", "0.7", "842.7"})
  @Order(1)
  void testIsValidInterestRateForSavingsAccount_invalidValues_false(String values) {
    var value = new BigDecimal(values);
    assertFalse(isValidInterestRateForSavingsAccount(value));
  }


  // ============================== Valid Interest Rate for Credit Card (>= 0.1) ==============================
  @ParameterizedTest
  @ValueSource(strings = {"0.1", "0.2638", "0.5", "2357"})
  @Order(2)
  void testIsValidInterestRateForCreditCard_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidInterestRateForCreditCard(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-0.3", "0", "0.0001", "0.009999999999"})
  @Order(2)
  void testIsValidInterestRateForCreditCard_invalidValues_false(String values) {
    var value = new BigDecimal(values);
    assertFalse(isValidInterestRateForCreditCard(value));
  }


}
