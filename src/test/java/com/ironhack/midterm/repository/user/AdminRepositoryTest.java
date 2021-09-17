package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.util.DbTestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AdminRepository adminRepository;

  private Admin a1;
  private Admin a2;
  private Admin a3;

  @BeforeEach
  void setUp() {
    a1 = new Admin("admin", "admin", "Admin");
    a2 = new Admin("superuser", "test1", "SU");
    a3 = new Admin("joaodss", "password", "Joaods");
    adminRepository.saveAll(List.of(a1, a2, a3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    adminRepository.deleteAll();
    DbTestUtil.resetAutoIncrementColumns(applicationContext, "user");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfAdminsInDatabase_correctAmount() {
    assertEquals(3, adminRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateAdmin_saveNewAdminWithOneOwner_storedInRepository() {
    var initialSize = adminRepository.count();
    adminRepository.save(new Admin("Test", "password", "Tester"));
    assertEquals(initialSize + 1, adminRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadAdmin_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = adminRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadAdmin_findById_validId_returnsObjectsWithSameId() {
    var element1 = adminRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadAdmin_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = adminRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateAdmin_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = adminRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setName("New name");
    adminRepository.save(element1.get());

    var updatedElement1 = adminRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals("New name", updatedElement1.get().getName());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteAdmin_deleteAdmin_validId_deletedFromRepository() {
    var initialSize = adminRepository.count();
    adminRepository.deleteById(2L);
    assertEquals(initialSize - 1, adminRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteAdmin_deleteAdmin_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> adminRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from AccountHolders ====================


  // ======================================== Custom Queries Testing ========================================


}