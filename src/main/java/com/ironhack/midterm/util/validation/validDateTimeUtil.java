package com.ironhack.midterm.util.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class validDateTimeUtil {

  // ============================== Valid Date/Time (before Now) ==============================
  public static boolean isValidDateTime(LocalDateTime creationDate) {
    return dateTimeNow().isAfter(creationDate.minus(10, ChronoUnit.SECONDS));
  }


  // ============================== Create Date/Time Now (London Time) ==============================
  public static LocalDateTime dateTimeNow() {
    return LocalDateTime.now(ZoneId.of("Europe/London"));
  }


}
