package com.ironhack.midterm.dao.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class ThirdPartyTest {
  @Test
  void testConstructor() {
    ThirdParty actualThirdParty = new ThirdParty("janedoe", "iloveyou", "Name");

    assertEquals("janedoe", actualThirdParty.getUsername());
    assertTrue(actualThirdParty.getRoles().isEmpty());
    assertEquals("Name", actualThirdParty.getName());
  }

  @Test
  void testEquals() {
    ThirdParty thirdParty = new ThirdParty();
    thirdParty.setPassword("iloveyou");
    thirdParty.setUsername("janedoe");
    thirdParty.setId(123L);
    thirdParty.setName("Name");
    thirdParty.setHashedKey("Hashed Key");
    thirdParty.setRoles(new HashSet<Role>());
    assertFalse(thirdParty.equals(null));
  }

  @Test
  void testEquals2() {
    ThirdParty thirdParty = new ThirdParty();
    thirdParty.setPassword("iloveyou");
    thirdParty.setUsername("janedoe");
    thirdParty.setId(123L);
    thirdParty.setName("Name");
    thirdParty.setHashedKey("Hashed Key");
    thirdParty.setRoles(new HashSet<Role>());
    assertFalse(thirdParty.equals("Different type to ThirdParty"));
  }

  @Test
  void testEquals3() {
    ThirdParty thirdParty = new ThirdParty();
    thirdParty.setPassword("iloveyou");
    thirdParty.setUsername("janedoe");
    thirdParty.setId(123L);
    thirdParty.setName("Name");
    thirdParty.setHashedKey("Hashed Key");
    thirdParty.setRoles(new HashSet<Role>());
    assertTrue(thirdParty.equals(thirdParty));
    int expectedHashCodeResult = thirdParty.hashCode();
    assertEquals(expectedHashCodeResult, thirdParty.hashCode());
  }

  @Test
  void testEquals4() {
    ThirdParty thirdParty = new ThirdParty();
    thirdParty.setPassword("iloveyou");
    thirdParty.setUsername("janedoe");
    thirdParty.setId(123L);
    thirdParty.setName("Name");
    thirdParty.setHashedKey("Hashed Key");
    thirdParty.setRoles(new HashSet<Role>());

    ThirdParty thirdParty1 = new ThirdParty();
    thirdParty1.setPassword("iloveyou");
    thirdParty1.setUsername("janedoe");
    thirdParty1.setId(123L);
    thirdParty1.setName("Name");
    thirdParty1.setHashedKey("Hashed Key");
    thirdParty1.setRoles(new HashSet<Role>());
    assertTrue(thirdParty.equals(thirdParty1));
    int expectedHashCodeResult = thirdParty.hashCode();
    assertEquals(expectedHashCodeResult, thirdParty1.hashCode());
  }

  @Test
  void testEquals5() {
    ThirdParty thirdParty = new ThirdParty();
    thirdParty.setPassword("iloveyou");
    thirdParty.setUsername("Name");
    thirdParty.setId(123L);
    thirdParty.setName("Name");
    thirdParty.setHashedKey("Hashed Key");
    thirdParty.setRoles(new HashSet<Role>());

    ThirdParty thirdParty1 = new ThirdParty();
    thirdParty1.setPassword("iloveyou");
    thirdParty1.setUsername("janedoe");
    thirdParty1.setId(123L);
    thirdParty1.setName("Name");
    thirdParty1.setHashedKey("Hashed Key");
    thirdParty1.setRoles(new HashSet<Role>());
    assertFalse(thirdParty.equals(thirdParty1));
  }
}

