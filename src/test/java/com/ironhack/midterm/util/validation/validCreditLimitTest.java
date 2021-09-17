package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.ironhack.midterm.util.validation.validCreditLimit.isValidCreditLimitForCreditCard;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class validCreditLimitTest {

  // ========================= Is Valid Credit Limit for Credit Card (>= 0, <= 100000) =========================
  @ParameterizedTest
  @ValueSource(strings = {"0", "0.01", "39359.568", "99999.99", "100000.00"})
  @Order(1)
  void testIsValidInterestRateForCreditCard_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidCreditLimitForCreditCard(value));
    assertTrue(isValidCreditLimitForCreditCard(new Money(value)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "-0.01", "100000.01", "257281.10"})
  @Order(1)
  void testIsValidInterestRateForCreditCard_invalidValues_false(String values) {
    BigDecimal value = new BigDecimal(values);
    System.out.println(value);
    assertFalse(isValidCreditLimitForCreditCard(value));
    assertFalse(isValidCreditLimitForCreditCard(new Money(value)));
  }


}
