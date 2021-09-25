package com.ironhack.midterm.util.database;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.repository.user.RoleRepository;
import com.ironhack.midterm.utils.DbResetUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
class DbTestUtilTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private RoleRepository roleRepository;

  @BeforeEach
  void setUp() throws SQLException {
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "roles");
    roleRepository.save(new Role("TEST"));
  }

  @AfterEach
  void tearDown() throws SQLException {
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "roles");
  }

  @Test
  void testDbTestUtility_deleteAllRolesCreateNewRole_noAutoIncrementReset_idKeepsIncrementing() {
    assertEquals(1, roleRepository.count());
    roleRepository.deleteAll();
    assertEquals(0, roleRepository.count());
    roleRepository.save(new Role("NEW_ROLE"));
    assertNotEquals(1, roleRepository.findAll().get(0).getId());
  }

  @Test
  void testDbTestUtility_deleteAllRolesCreateNewRole_withAutoIncrementReset_idResets() throws SQLException {
    assertEquals(1, roleRepository.count());
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "roles");
    assertEquals(0, roleRepository.count());
    roleRepository.save(new Role("NEW_ROLE"));
    assertEquals(1, roleRepository.findAll().get(0).getId());
  }


}