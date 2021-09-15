package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.ThirdParty;
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
class ThirdPartyRepositoryTest {

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
  void tearDown() {
    thirdPartyRepository.deleteAll();
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
  void testReadThirdParty_findById_returnsObjectsWithSameId() {
    var element1 = thirdPartyRepository.findById(2L);
    if (element1.isPresent()) {
      assertEquals(2L, element1.get().getId());
    } else throw new TestInstantiationException("Id not found");
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateThirdParty_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = thirdPartyRepository.findById(3L);
    if (element1.isPresent()) {
      element1.get().setName("New name");
      thirdPartyRepository.save(element1.get());
    } else throw new TestInstantiationException("Id not found");

    var updatedElement1 = thirdPartyRepository.findById(3L);
    if (updatedElement1.isPresent()) {
      assertEquals("New name", updatedElement1.get().getName());
    } else throw new TestInstantiationException("Updated id not found");
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteThirdParty_deleteThirdParty_deletedFromRepository() {
    var initialSize = thirdPartyRepository.count();
    thirdPartyRepository.deleteById(2L);
    assertEquals(initialSize - 1, thirdPartyRepository.count());
  }


}