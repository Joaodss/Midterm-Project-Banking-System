package com.ironhack.midterm.util;

import com.ironhack.midterm.util.DateTimeUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;

import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DateTimeUtilTest {

  // ============================== Is Valid Date/Time (before Now) ==============================
  @Test
  @Order(1)
  void testIsValidDateTime_validDateTime_true() {
    assertTrue(DateTimeUtil.isValidDateTime(LocalDateTime.of(2020, 1, 1, 1, 1)));
  }

  @Test
  @Order(1)
  void testIsValidDateTime_nowDateTime_true() {
    assertTrue(DateTimeUtil.isValidDateTime(dateTimeNow()));
  }

  @Test
  @Order(1)
  void testIsValidDateTime_invalidDateTime_false() {
    assertFalse(DateTimeUtil.isValidDateTime(dateTimeNow().plusMinutes(10)));
  }


}
