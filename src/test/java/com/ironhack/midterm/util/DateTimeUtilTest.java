package com.ironhack.midterm.util;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DateTimeUtilTest {

  @Test
  @Order(1)
  void testDateTimeNow() {
    // If it gives error run again. Might be between seconds, due to the time between operations.
    assertEquals(LocalDateTime.now(ZoneId.of("Europe/London")).truncatedTo(ChronoUnit.SECONDS), dateTimeNow().truncatedTo(ChronoUnit.SECONDS));
  }

}
