package com.ironhack.midterm.util;

import org.junit.jupiter.api.Test;

import static com.ironhack.midterm.util.UserUtil.isSameUserName;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUtilTest {

  @Test
  void isSameUserName_differentNames_sameCharacters() {
    String name1 = "João Afonso silva";
    String name2 = "J oão afonso silva";
    assertTrue(isSameUserName(name1, name2));
  }

  @Test
  void isSameUserName_differentNames_differentCharacters() {
    String name1 = "João silva";
    String name2 = "J oão afonso silva";
    assertFalse(isSameUserName(name1, name2));
  }

}
