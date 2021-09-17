package com.ironhack.midterm.util.money;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newBD;
import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newMoney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyHelperTest {

  // ======================================== new Money ========================================
  @ParameterizedTest
  @ValueSource(strings = {"0", "3.333", "235723572.5721571"})
  void testNewMoney_validStringNumber(String value) {
    assertEquals(new Money(new BigDecimal(value)), newMoney(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "ah3haefhb", "157135,13532"})
  void testNewMoney_invalidStringNumber(String value) {
    assertThrows(NumberFormatException.class, () -> {
      var money = newMoney(value);
    });
  }

  @Test
  void testNewMoney_toString() {
    Money actualNewMoneyResult = MoneyInitializerUtil.newMoney("42");
    assertEquals("€ 42.00", actualNewMoneyResult.toString());
    assertEquals("42.00", actualNewMoneyResult.getAmount().toString());
  }

  // ======================================== new BigDecimal ========================================
  @ParameterizedTest
  @ValueSource(strings = {"0", "3.333", "235723572.5721571"})
  void testNewBD_validStringNumber(String value) {
    assertEquals(new BigDecimal(value), newBD(value));
    assertEquals(new BigDecimal(value).setScale(3, RoundingMode.HALF_EVEN), newBD(value, 3));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "ah3haefhb", "157135,13532"})
  void testNewBD_invalidStringNumber(String value) {
    assertThrows(NumberFormatException.class, () -> {
      var bd1 = newBD(value);
      var bd2 = newBD(value, 2);
    });
  }

  @Test
  void testNewBD_toString() {
    assertEquals("42", MoneyInitializerUtil.newBD("42").toString());
    assertEquals("42.00", MoneyInitializerUtil.newBD("42", 2).toString());
    assertEquals("42", MoneyInitializerUtil.newBD("42", 0).toString());
  }


}
