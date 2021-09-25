package com.ironhack.midterm.utils;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.repository.user.AdminRepository;
import com.ironhack.midterm.repository.user.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DbTestUtilTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private RoleRepository roleRepository;


  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() throws SQLException {
    adminRepository.deleteAll();
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "user", "roles");
  }


  @Test
  @Order(1)
  void testDbTestUtility_deleteAllRolesCreateNewRole_noAutoIncrementReset_idKeepsIncrementing() {
    assertEquals(0, roleRepository.count());

    // Create First
    roleRepository.save(new Role("ROLE"));

    // Read First
    assertEquals(1, roleRepository.findAll().get(0).getId());
    assertEquals("ROLE", roleRepository.findAll().get(0).getName());

    // Delete All
    roleRepository.deleteAll();
    assertEquals(0, roleRepository.count());

    // Create Second
    roleRepository.save(new Role("NEW_ROLE"));

    // Read Second
    assertNotEquals(1, roleRepository.findAll().get(0).getId());
    assertEquals(2, roleRepository.findAll().get(0).getId());
    assertEquals("NEW_ROLE", roleRepository.findAll().get(0).getName());
  }

  @Test
  @Order(2)
  void testCreateDeleteCreateCreditCard_singleTable_saveNewOwner_deleteAndReset_idITheSame() throws SQLException {
    assertEquals(0, adminRepository.count());

    // Create First
    adminRepository.save(new Admin("admin", "admin", "Admin"));

    // Read First
    assertEquals(1, adminRepository.count());

    var firstAdmin = adminRepository.findById(1L);
    assertTrue(firstAdmin.isPresent());
    assertEquals(1, firstAdmin.get().getId());
    assertEquals("Admin", firstAdmin.get().getName());

    // Delete All
    adminRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "user");
    assertEquals(0, adminRepository.count());

    // Create Second
    adminRepository.save(new Admin("admin2", "admin2", "Admin2"));

    // Read Second
    assertEquals(1, adminRepository.count());

    var secondAdmin = adminRepository.findById(1L);
    assertTrue(secondAdmin.isPresent());
    assertEquals(1, secondAdmin.get().getId());
    assertEquals("Admin2", secondAdmin.get().getName());
  }

  @Test
  @Order(2)
  void testCreateDeleteCreateCreditCard_multipleTables_saveNewOwner_deleteAndReset_idITheSame() throws SQLException {
    assertEquals(0, adminRepository.count());

    // Create First
    var role1 = new Role("ADMIN");
    roleRepository.save(role1);
    var admin1 = new Admin("admin", "admin", "Admin");
    admin1.getRoles().add(role1);
    adminRepository.save(admin1);

    // Read First
    assertEquals(1, adminRepository.count());
    assertEquals(1, roleRepository.count());

    var firstAdmin = adminRepository.findById(1L);
    assertTrue(firstAdmin.isPresent());
    assertEquals(1, firstAdmin.get().getId());
    assertEquals("Admin", firstAdmin.get().getName());
    assertEquals(1, firstAdmin.get().getRoles().stream().findFirst().get().getId());
    assertEquals("ADMIN", firstAdmin.get().getRoles().stream().findFirst().get().getName());

    // Delete All
    adminRepository.deleteAll();
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "user", "roles");
    assertEquals(0, adminRepository.count());

    // Create Second
    var role2 = new Role("ADMIN2");
    roleRepository.save(role2);
    var admin2 = new Admin("admin2", "admin2", "Admin2");
    admin2.getRoles().add(role2);
    adminRepository.save(admin2);

    // Read Second
    assertEquals(1, adminRepository.count());
    assertEquals(1, roleRepository.count());

    var secondAdmin = adminRepository.findById(1L);
    assertTrue(secondAdmin.isPresent());
    assertEquals(1, secondAdmin.get().getId());
    assertEquals("Admin2", secondAdmin.get().getName());
    assertEquals(1, secondAdmin.get().getRoles().stream().findFirst().get().getId());
    assertEquals("ADMIN2", secondAdmin.get().getRoles().stream().findFirst().get().getName());
  }


}