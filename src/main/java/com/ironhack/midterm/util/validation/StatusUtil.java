package com.ironhack.midterm.util.validation;

import com.ironhack.midterm.enums.AccountStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusUtil {

  // ============================== Valid Status from String (exists) ==============================
  public static boolean isValidStatusFromString(String stringStatus) {
    for (AccountStatus s : AccountStatus.values()) {
      if (s.name().equalsIgnoreCase(stringStatus))
        return true;
    }
    return false;
  }

  // =================================== Get Status from String ===================================
  public static AccountStatus statusFromString(String stringStatus) {
    for (AccountStatus s : AccountStatus.values()) {
      if (s.name().equalsIgnoreCase(stringStatus))
        return s;
    }
    throw new IllegalArgumentException("Status " + stringStatus + " does not exist.");
  }


}
