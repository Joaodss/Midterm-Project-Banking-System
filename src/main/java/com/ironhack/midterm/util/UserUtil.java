package com.ironhack.midterm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUtil {

  public static boolean isSameUserName(String user1, String user2) {
    String user1Cleaned = user1.trim().replace(" ", "");
    String user2Cleaned = user2.trim().replace(" ", "");
    return user1Cleaned.equalsIgnoreCase(user2Cleaned);
  }



}
