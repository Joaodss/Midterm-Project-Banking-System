package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.ironhack.midterm.util.validation.validMonthlyMaintenanceFee.isValidMonthlyMaintenanceFee;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class validMonthlyMaintenanceFeeTest {

  // =================================== Is Valid Monthly Maintenance Fee ===================================
  @ParameterizedTest
  @ValueSource(strings = {"0", "0.01", "1", "35723.677"})
  @Order(1)
  void testIsValidMonthlyMaintenanceFee_validValues_true(String values) {
    var value = new BigDecimal(values);
    assertTrue(isValidMonthlyMaintenanceFee(value));
    assertTrue(isValidMonthlyMaintenanceFee(new Money(value)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "-1", "-35723.677"})
  @Order(1)
  void testIsValidMonthlyMaintenanceFee_invalidValues_false(String values) {
    BigDecimal value = new BigDecimal(values);
    System.out.println(value);
    assertFalse(isValidMonthlyMaintenanceFee(value));
    assertFalse(isValidMonthlyMaintenanceFee(new Money(value)));
  }


}