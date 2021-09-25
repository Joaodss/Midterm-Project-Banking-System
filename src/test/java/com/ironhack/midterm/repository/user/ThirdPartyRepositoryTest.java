package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.utils.DbResetUtil;
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

  @Autowired
  private RoleRepository roleRepository;

  private Role r1;
  private Role r2;

  @BeforeEach
  void setUp() {
    r1 = new Role("USER");
    r2 = new Role("ADMIN");
    roleRepository.saveAll(List.of(r1, r2));

    ThirdParty tp1 = new ThirdParty("thirdParty", "thirdParty", "thirdParty");
    tp1.getRoles().add(r1);
    tp1.getRoles().add(r2);
    ThirdParty tp2 = new ThirdParty("superuser", "test1", "SU");
    tp2.getRoles().add(r1);
    ThirdParty tp3 = new ThirdParty("joaodss", "password", "João Afonso");
    thirdPartyRepository.saveAll(List.of(tp1, tp2, tp3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    thirdPartyRepository.deleteAll();
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "user", "roles");
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
  void testReadThirdParty_findById_invalidId_returnsEmpty() {
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
  // ==================== Read from Roles ====================
  @Test
  @Order(6)
  void testReadFromRoles_findAll_returnThirdPartyWithSetOfRoles() {
    var element1 = thirdPartyRepository.findAll();
    assertFalse(element1.isEmpty());
    assertTrue(element1.get(0).getRoles().contains(r1));
    assertTrue(element1.get(0).getRoles().contains(r2));
  }

  @Test
  @Order(6)
  void testReadFromRoles_findById_returnThirdPartyWithItsSetOfRoles() {
    var element1 = thirdPartyRepository.findById(1L);
    assertTrue(element1.isPresent());
    assertTrue(element1.get().getRoles().contains(r1));
  }

  @Test
  @Order(6)
  void testReadFromRoles_findById_returnThirdPartyWithItsSetOfRoles_roleWithCorrectName() {
    var element1 = thirdPartyRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertTrue(element1.get().getRoles().stream().findFirst().isPresent());
    assertEquals("USER", element1.get().getRoles().stream().findFirst().get().getName());
  }


  // ======================================== Custom Queries Testing ========================================
  // ==================== Find By Username ====================
  @Test
  @Order(7)
  void testFindByUsername_usernameIsValid_returnCorrectAccount() {
    var element1 = thirdPartyRepository.findByUsername("joaodss");
    assertTrue(element1.isPresent());
    assertEquals("João Afonso", element1.get().getName());
  }

  @Test
  @Order(7)
  void testFindByUsername_usernameIsInvalid_returnEmpty() {
    var element1 = thirdPartyRepository.findByUsername("no_username");
    assertTrue(element1.isEmpty());
  }

}