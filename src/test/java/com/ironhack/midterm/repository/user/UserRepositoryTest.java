package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.utils.DbResetUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private ThirdPartyRepository thirdPartyRepository;

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private RoleRepository roleRepository;

  @BeforeEach
  void setUp() {
    Address pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    Address pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");

    Role r1 = new Role("USER");
    Role r2 = new Role("ADMIN");
    Role r3 = new Role("THIRD_PARTY");
    roleRepository.saveAll(List.of(r1, r2, r3));

    Admin a1 = new Admin("admin", "admin", "Admin");
    a1.getRoles().add(r2);
    adminRepository.save(a1);

    ThirdParty tp1 = new ThirdParty("revolut", "revolut", "Revolut");
    tp1.getRoles().add(r3);
    thirdPartyRepository.save(tp1);

    AccountHolder ah1 = new AccountHolder("joaodss", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa2);
    ah1.getRoles().add(r1);
    AccountHolder ah2 = new AccountHolder("anamaria", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    ah2.getRoles().add(r1);
    accountHolderRepository.saveAll(List.of(ah1, ah2));
  }

  @AfterEach
  void tearDown() throws SQLException {
    accountHolderRepository.deleteAll();
    thirdPartyRepository.deleteAll();
    adminRepository.deleteAll();
    userRepository.deleteAll();
    roleRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "user", "roles");
  }

  // ======================================== READ and DELETE TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfUsersInDatabase_correctAmount() {
    assertEquals(4, userRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(2)
  void testReadUsers_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = userRepository.findAll();
    assertFalse(allElements.isEmpty());
    assertEquals(4, allElements.size());
  }

  @Test
  @Order(2)
  void testReadUser_findById_validId_returnsObjectsWithSameId() {
    var element1 = userRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(2)
  void testReadUser_findById_invalidId_returnsEmpty() {
    var element1 = userRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Custom Read ====================
  @Test
  @Order(3)
  void testReadUser_findByUsername_validIdAccountHolder_returnsCorrectObject() {
    var element1 = userRepository.findByUsername("joaodss");
    assertTrue(element1.isPresent());
    assertEquals("João Afonso", element1.get().getName());
    assertEquals(AccountHolder.class, element1.get().getClass());
  }

  @Test
  @Order(3)
  void testReadUser_findByUsername_validIdAdmin_returnsCorrectObject() {
    var element1 = userRepository.findByUsername("admin");
    assertTrue(element1.isPresent());
    assertEquals("Admin", element1.get().getName());
    assertEquals(Admin.class, element1.get().getClass());
  }

  @Test
  @Order(3)
  void testReadUser_findByUsername_invalidId_returnsEmpty() {
    var element1 = userRepository.findByUsername("no user");
    assertTrue(element1.isEmpty());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteUser_deleteThirdParty_validId_deletedFromRepository() {
    var initialSize = userRepository.count();
    userRepository.deleteById(2L);
    assertEquals(initialSize - 1, userRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteUser_deleteUser_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> userRepository.deleteById(99L));
  }

}