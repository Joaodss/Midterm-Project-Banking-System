package com.ironhack.midterm.util;

import com.ironhack.midterm.model.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.ironhack.midterm.util.MoneyHelper.newBD;
import static com.ironhack.midterm.util.MoneyHelper.newMoney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyHelperTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

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
      var bd2 = newBD(value,2);
    });
  }


}