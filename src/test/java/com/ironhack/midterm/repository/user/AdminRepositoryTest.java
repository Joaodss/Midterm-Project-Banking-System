package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.Admin;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)   // Resets DB and id generation (slower)
class AdminRepositoryTest {

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
  void tearDown() {
    adminRepository.deleteAll();
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
  void testReadAdmin_findById_returnsObjectsWithSameId() {
    var element1 = adminRepository.findById(2L);
    if (element1.isPresent()) {
      assertEquals(2L, element1.get().getId());
    } else throw new TestInstantiationException("Id not found");
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateAdmin_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = adminRepository.findById(3L);
    if (element1.isPresent()) {
      element1.get().setName("New name");
      adminRepository.save(element1.get());
    } else throw new TestInstantiationException("Id not found");

    var updatedElement1 = adminRepository.findById(3L);
    if (updatedElement1.isPresent()) {
      assertEquals("New name", updatedElement1.get().getName());
    } else throw new TestInstantiationException("Updated id not found");
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteAdmin_deleteAdmin_deletedFromRepository() {
    var initialSize = adminRepository.count();
    adminRepository.deleteById(2L);
    assertEquals(initialSize - 1, adminRepository.count());
  }


}