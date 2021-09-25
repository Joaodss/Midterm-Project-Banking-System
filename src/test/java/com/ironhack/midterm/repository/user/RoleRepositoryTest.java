package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.Role;
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
class RoleRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    Role r1 = new Role("USER");
    Role r2 = new Role("ADMIN");
    roleRepository.saveAll(List.of(r1, r2));
  }

  @AfterEach
  void tearDown() throws SQLException {
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "roles");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfRolesInDatabase_correctAmount() {
    assertEquals(2, roleRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateRole_saveNewRoleWithOneAddress_storedInRepository() {
    var initialSize = roleRepository.count();
    roleRepository.save(new Role("THIRD_PARTY"));
    assertEquals(initialSize + 1, roleRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadRole_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = roleRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadRole_findById_validId_returnsObjectsWithCorrectName() {
    var element1 = roleRepository.findById(2);
    assertTrue(element1.isPresent());
    assertEquals("ADMIN", element1.get().getName());
  }

  @Test
  @Order(3)
  void testReadRole_findById_invalidId_returnsEmpty() {
    var element1 = roleRepository.findById(99);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateRole_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = roleRepository.findById(1);
    assertTrue(element1.isPresent());
    element1.get().setName("NEW_ROLE");
    roleRepository.save(element1.get());

    var updatedElement1 = roleRepository.findById(1);
    assertTrue(updatedElement1.isPresent());
    assertEquals("NEW_ROLE", updatedElement1.get().getName());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteRole_deleteRole_validId_deletedFromRepository() {
    var initialSize = roleRepository.count();
    roleRepository.deleteById(2);
    assertEquals(initialSize - 1, roleRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteRole_deleteRole_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> roleRepository.deleteById(99));
  }


  // ======================================== Custom Queries Testing ========================================
  // ==================== Find By Name ====================
  @Test
  @Order(7)
  void testFindByUsername_usernameIsValid_returnCorrectAccount() {
    var element1 = roleRepository.findByName("USER");
    assertTrue(element1.isPresent());
    assertEquals("USER", element1.get().getName());
  }

  @Test
  @Order(7)
  void testFindByUsername_usernameIsInvalid_returnEmpty() {
    var element1 = roleRepository.findByName("NOOOOO");
    assertTrue(element1.isEmpty());
  }

}
