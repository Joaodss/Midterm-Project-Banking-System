package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.util.database.DbResetUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentCheckingAccountRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private StudentCheckingAccountRepository studentCheckingAccountRepository;


  private AccountHolder ah1;
  private AccountHolder ah2;

  private StudentCheckingAccount sca1;
  private StudentCheckingAccount sca2;
  private StudentCheckingAccount sca3;


  @BeforeEach
  void setUp() {
    var pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    var pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");
    ah1 = new AccountHolder("joa0ds5", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa1);
    ah2 = new AccountHolder("an5m6ri7", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    accountHolderRepository.saveAll(List.of(ah1, ah2));

    sca1 = new StudentCheckingAccount(newMoney("2000"), ah1);
    sca2 = new StudentCheckingAccount(newMoney("500"), ah1, ah2);
    sca3 = new StudentCheckingAccount(newMoney("1000"), ah2);
    studentCheckingAccountRepository.saveAll(List.of(sca1, sca2, sca3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    studentCheckingAccountRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "account", "user");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfStudentCheckingAccountsInDatabase_correctAmount() {
    assertEquals(3, studentCheckingAccountRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateStudentCheckingAccount_saveNewStudentCheckingAccountWithOneOwner_storedInRepository() throws NoSuchAlgorithmException {
    var initialSize = studentCheckingAccountRepository.count();
    studentCheckingAccountRepository.save(new StudentCheckingAccount(newMoney("2500"), ah1));
    assertEquals(initialSize + 1, studentCheckingAccountRepository.count());
  }

  @Test
  @Order(2)
  void testCreateStudentCheckingAccount_saveNewStudentCheckingAccountWithTwoOwner_storedInRepository() throws NoSuchAlgorithmException {
    var initialSize = studentCheckingAccountRepository.count();
    studentCheckingAccountRepository.save(new StudentCheckingAccount(newMoney("7000"), ah1, ah2));
    assertEquals(initialSize + 1, studentCheckingAccountRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadStudentCheckingAccount_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = studentCheckingAccountRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadStudentCheckingAccount_findById_validId_returnsObjectsWithSameId() {
    var element1 = studentCheckingAccountRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadStudentCheckingAccount_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = studentCheckingAccountRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateStudentCheckingAccount_changeBalance_newMinBalanceEqualsDefinedValue() {
    var element1 = studentCheckingAccountRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setAccountStatus(AccountStatus.FROZEN);
    studentCheckingAccountRepository.save(element1.get());

    var updatedElement1 = studentCheckingAccountRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals(AccountStatus.FROZEN, updatedElement1.get().getAccountStatus());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteStudentCheckingAccount_deleteStudentCheckingAccount_validId_deletedFromRepository() {
    var initialSize = studentCheckingAccountRepository.count();
    studentCheckingAccountRepository.deleteById(2L);
    assertEquals(initialSize - 1, studentCheckingAccountRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteStudentCheckingAccount_deleteStudentCheckingAccount_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> studentCheckingAccountRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from AccountHolders ====================
  @Test
  @Order(6)
  void testReadFromAccountHolders_findAllJoined_returnStudentCheckingAccountsWithAccountHolders() {
    var element1 = studentCheckingAccountRepository.findAllJoined();
    assertFalse(element1.isEmpty());
    assertEquals(ah1, element1.get(0).getPrimaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnStudentCheckingAccountWithPrimaryAccountHolder() {
    var element1 = studentCheckingAccountRepository.findByIdJoined(1);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertNull(element1.get().getSecondaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnStudentCheckingAccountWithAccountHolders() {
    var element1 = studentCheckingAccountRepository.findByIdJoined(2);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertEquals(ah2, element1.get().getSecondaryOwner());
  }


  // ======================================== Custom Queries Testing ========================================


}
