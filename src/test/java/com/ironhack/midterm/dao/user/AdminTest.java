package com.ironhack.midterm.dao.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class AdminTest {
  @Test
  void testConstructor() {
    Admin actualAdmin = new Admin("janedoe", "iloveyou", "Name");

    assertEquals("janedoe", actualAdmin.getUsername());
    assertTrue(actualAdmin.getRoles().isEmpty());
    assertEquals("Name", actualAdmin.getName());
  }

  @Test
  void testEquals() {
    Admin admin = new Admin();
    admin.setPassword("iloveyou");
    admin.setUsername("janedoe");
    admin.setId(123L);
    admin.setName("Name");
    admin.setRoles(new HashSet<Role>());
    assertFalse(admin.equals(null));
  }

  @Test
  void testEquals2() {
    Admin admin = new Admin();
    admin.setPassword("iloveyou");
    admin.setUsername("janedoe");
    admin.setId(123L);
    admin.setName("Name");
    admin.setRoles(new HashSet<Role>());
    assertFalse(admin.equals("Different type to Admin"));
  }

  @Test
  void testEquals3() {
    Admin admin = new Admin();
    admin.setPassword("iloveyou");
    admin.setUsername("janedoe");
    admin.setId(123L);
    admin.setName("Name");
    admin.setRoles(new HashSet<Role>());
    assertTrue(admin.equals(admin));
    int expectedHashCodeResult = admin.hashCode();
    assertEquals(expectedHashCodeResult, admin.hashCode());
  }

  @Test
  void testEquals4() {
    Admin admin = new Admin();
    admin.setPassword("iloveyou");
    admin.setUsername("janedoe");
    admin.setId(123L);
    admin.setName("Name");
    admin.setRoles(new HashSet<Role>());

    Admin admin1 = new Admin();
    admin1.setPassword("iloveyou");
    admin1.setUsername("janedoe");
    admin1.setId(123L);
    admin1.setName("Name");
    admin1.setRoles(new HashSet<Role>());
    assertTrue(admin.equals(admin1));
    int expectedHashCodeResult = admin.hashCode();
    assertEquals(expectedHashCodeResult, admin1.hashCode());
  }

  @Test
  void testEquals5() {
    Admin admin = new Admin();
    admin.setPassword("iloveyou");
    admin.setUsername("Name");
    admin.setId(123L);
    admin.setName("Name");
    admin.setRoles(new HashSet<Role>());

    Admin admin1 = new Admin();
    admin1.setPassword("iloveyou");
    admin1.setUsername("janedoe");
    admin1.setId(123L);
    admin1.setName("Name");
    admin1.setRoles(new HashSet<Role>());
    assertFalse(admin.equals(admin1));
  }
}

