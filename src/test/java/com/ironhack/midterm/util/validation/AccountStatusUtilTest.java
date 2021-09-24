package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.enums.AccountStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.ironhack.midterm.util.EnumsUtil.isValidAccountStatusFromString;
import static com.ironhack.midterm.util.EnumsUtil.accountStatusFromString;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountStatusUtilTest {

  // ============================== Is Valid Status from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"active", "ActIVe", "Frozen", "FROZEN"})
  @Order(1)
  void testIsValidStatusFromString_validStatus_true(String status) {
    assertTrue(isValidAccountStatusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "Fro_zen", "a c t i v e", "blabla"})
  @Order(1)
  void testIsValidStatusFromString_invalidStatus_false(String status) {
    assertFalse(isValidAccountStatusFromString(status));
  }


  // ============================== Is Valid Status from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"active", "ActIVe"})
  @Order(2)
  void testStatusFromString_validActiveStatus_returnActiveStatus(String status) {
    assertEquals(AccountStatus.ACTIVE, accountStatusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Frozen", "FROZEN"})
  @Order(2)
  void testStatusFromString_validFrozenStatus_returnFrozenStatus(String status) {
    assertEquals(AccountStatus.FROZEN, accountStatusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "Fro_zen", "a c t i v e", "blabla"})
  @Order(2)
  void testStatusFromString_invalidStatus_throwException(String status) {
    assertThrows(IllegalArgumentException.class, () -> accountStatusFromString(status));
  }


}
