package com.ironhack.midterm.util;

import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.enums.TransactionType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.ironhack.midterm.util.EnumsUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnumUtilTest {

  // ============================== AccountStatus from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"active", "ActIVe"})
  @Order(1)
  void testAccountStatusFromString_validActiveStatus_returnACTIVE(String status) {
    assertEquals(AccountStatus.ACTIVE, accountStatusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Frozen", "FROZEN"})
  @Order(1)
  void testAccountStatusFromString_validFrozenStatus_returnFROZEN(String status) {
    assertEquals(AccountStatus.FROZEN, accountStatusFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "Fro_zen", "a c t i v e", "blabla"})
  @Order(1)
  void testAccountStatusFromString_invalidStatus_throwException(String status) {
    assertThrows(IllegalArgumentException.class, () -> accountStatusFromString(status));
  }


  // ============================== TransactionPurpose from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"send", "SeND"})
  @Order(2)
  void testTransactionPurposeFromString_validSendTransactionPurpose_returnSEND(String transactionPurpose) {
    assertEquals(TransactionPurpose.SEND, transactionPurposeFromString(transactionPurpose));
  }

  @ParameterizedTest
  @ValueSource(strings = {"REQUEST", "ReQuESt"})
  @Order(2)
  void testTransactionPurposeFromString_validRequestTransactionPurpose_returnREQUEST(String transactionPurpose) {
    assertEquals(TransactionPurpose.REQUEST, transactionPurposeFromString(transactionPurpose));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "reuqest", "reques", "asend"})
  @Order(2)
  void testTransactionPurposeFromString_invalidTransactionPurpose_throwException(String transactionPurpose) {
    assertThrows(IllegalArgumentException.class, () -> transactionPurposeFromString(transactionPurpose));
  }


  // ============================== Is Valid TransactionType from String ==============================
  @ParameterizedTest
  @ValueSource(strings = {"SEND_TO_THiRD_pArTY", "SEND_TO_THIRD_PARTY", "send_to_third_party"})
  @Order(2)
  void testTransactionTypeFromString_validActiveTransactionType_returnSEND_TO_THIRD_PARTY(String status) {
    assertEquals(TransactionType.SEND_TO_THIRD_PARTY, transactionTypeFromString(status));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "SEND TO THIRD PARTY", "a c t i v e", "blabla"})
  @Order(2)
  void testTransactionTypeFromString_invalidTransactionType_throwException(String status) {
    assertThrows(IllegalArgumentException.class, () -> transactionTypeFromString(status));
  }


}
