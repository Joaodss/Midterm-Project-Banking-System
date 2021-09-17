package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.ThirdParty;
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
class ThirdPartyRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private ThirdPartyRepository thirdPartyRepository;

  private ThirdParty tp1;
  private ThirdParty tp2;
  private ThirdParty tp3;

  @BeforeEach
  void setUp() {
    tp1 = new ThirdParty("thirdParty", "thirdParty", "thirdParty");
    tp2 = new ThirdParty("superuser", "test1", "SU");
    tp3 = new ThirdParty("joaodss", "password", "Joaods");
    thirdPartyRepository.saveAll(List.of(tp1, tp2, tp3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    thirdPartyRepository.deleteAll();
    DbTestUtil.resetAutoIncrementColumns(applicationContext, "user");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfThirdPartiesInDatabase_correctAmount() {
    assertEquals(3, thirdPartyRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateThirdParty_saveNewThirdPartyWithOneOwner_storedInRepository() {
    var initialSize = thirdPartyRepository.count();
    thirdPartyRepository.save(new ThirdParty("Test", "password", "Tester"));
    assertEquals(initialSize + 1, thirdPartyRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadThirdParty_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = thirdPartyRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadThirdParty_findById_validId_returnsObjectsWithSameId() {
    var element1 = thirdPartyRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadThirdParty_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = thirdPartyRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateThirdParty_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = thirdPartyRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setName("New name");
    thirdPartyRepository.save(element1.get());

    var updatedElement1 = thirdPartyRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals("New name", updatedElement1.get().getName());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteThirdParty_deleteThirdParty_validId_deletedFromRepository() {
    var initialSize = thirdPartyRepository.count();
    thirdPartyRepository.deleteById(2L);
    assertEquals(initialSize - 1, thirdPartyRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteThirdParty_deleteThirdParty_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> thirdPartyRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from AccountHolders ====================


  // ======================================== Custom Queries Testing ========================================


}