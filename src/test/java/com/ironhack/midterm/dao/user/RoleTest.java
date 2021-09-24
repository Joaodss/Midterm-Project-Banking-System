package com.ironhack.midterm.dao.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RoleTest {
  @Test
  void testConstructor() {
    Role actualRole = new Role("Name");
    assertNull(actualRole.getId());
    assertEquals("Role(id=null, name=Name)", actualRole.toString());
    assertTrue(actualRole.getUsers().isEmpty());
    assertEquals("Name", actualRole.getName());
  }
}

