package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.enums.Status;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.ironhack.midterm.util.validation.StatusUtil.isValidStatusFromString;
import static com.ironhack.midterm.util.validation.StatusUtil.statusFromString;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatusUtilTest {

  // ============================== Is Valid Status from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"active", "ActIVe", "Frozen", "FROZEN"})
  @Order(1)
  void testIsValidStatusFromString_validStatus_true(String status) {
    assertTrue(isValidStatusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "Fro_zen", "a c t i v e", "blabla"})
  @Order(1)
  void testIsValidStatusFromString_invalidStatus_false(String status) {
    assertFalse(isValidStatusFromString(status));
  }


  // ============================== Is Valid Status from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"active", "ActIVe"})
  @Order(2)
  void testStatusFromString_validActiveStatus_returnActiveStatus(String status) {
    assertEquals(Status.ACTIVE, statusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Frozen", "FROZEN"})
  @Order(2)
  void testStatusFromString_validFrozenStatus_returnFrozenStatus(String status) {
    assertEquals(Status.FROZEN, statusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "Fro_zen", "a c t i v e", "blabla"})
  @Order(2)
  void testStatusFromString_invalidStatus_throwException(String status) {
    assertThrows(IllegalArgumentException.class, () -> statusFromString(status));
  }


}
