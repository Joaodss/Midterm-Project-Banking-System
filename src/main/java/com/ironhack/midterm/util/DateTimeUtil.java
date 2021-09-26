package com.ironhack.midterm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtil {

  // ============================== Create Date/Time Now (London Time) ==============================
  public static LocalDateTime dateTimeNow() {
    return LocalDateTime.now(ZoneId.of("Europe/London"));
  }


}
