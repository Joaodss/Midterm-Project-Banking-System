package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.util.database.DbTestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.helper.MoneyHelper.newMoney;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckingAccountRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private CheckingAccountRepository checkingAccountRepository;


  private AccountHolder ah1;
  private AccountHolder ah2;

  private CheckingAccount ca1;
  private CheckingAccount ca2;
  private CheckingAccount ca3;


  @BeforeEach
  void setUp() {
    var pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    var pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");
    ah1 = new AccountHolder("joa0ds5", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa1);
    ah2 = new AccountHolder("an5m6ri7", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    accountHolderRepository.saveAll(List.of(ah1, ah2));

    ca1 = new CheckingAccount(newMoney("2000"), ah1, "abcdef123");
    ca2 = new CheckingAccount(newMoney("500"), ah1, ah2, "secretword");
    ca3 = new CheckingAccount(newMoney("1000"), ah2, "password123");
    checkingAccountRepository.saveAll(List.of(ca1, ca2, ca3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    checkingAccountRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbTestUtil.resetAutoIncrementColumns(applicationContext, "account", "user");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfCheckingAccountsInDatabase_correctAmount() {
    assertEquals(3, checkingAccountRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateCheckingAccount_saveNewCheckingAccountWithOneOwner_storedInRepository() {
    var initialSize = checkingAccountRepository.count();
    checkingAccountRepository.save(new CheckingAccount(newMoney("2500"), ah1, "testTest"));
    assertEquals(initialSize + 1, checkingAccountRepository.count());
  }

  @Test
  @Order(2)
  void testCreateCheckingAccount_saveNewCheckingAccountWithTwoOwner_storedInRepository() {
    var initialSize = checkingAccountRepository.count();
    checkingAccountRepository.save(new CheckingAccount(newMoney("7000"), ah1, ah2, "testTest2"));
    assertEquals(initialSize + 1, checkingAccountRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadCheckingAccount_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = checkingAccountRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadCheckingAccount_findById_validId_returnsObjectsWithSameId() {
    var element1 = checkingAccountRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadCheckingAccount_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = checkingAccountRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateCheckingAccount_changeMinBalance_newMinBalanceEqualsDefinedValue() {
    var element1 = checkingAccountRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setMinimumBalance(newMoney("180"));
    checkingAccountRepository.save(element1.get());

    var updatedElement1 = checkingAccountRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals(newMoney("180"), updatedElement1.get().getMinimumBalance());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteCheckingAccount_deleteCheckingAccount_validId_deletedFromRepository() {
    var initialSize = checkingAccountRepository.count();
    checkingAccountRepository.deleteById(2L);
    assertEquals(initialSize - 1, checkingAccountRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteCheckingAccount_deleteCheckingAccount_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> checkingAccountRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from AccountHolders ====================
  @Test
  @Order(6)
  void testReadFromAccountHolders_findAllJoined_returnCheckingAccountsWithAccountHolders() {
    var element1 = checkingAccountRepository.findAllJoined();
    assertFalse(element1.isEmpty());
    assertEquals(ah1, element1.get(0).getPrimaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnCheckingAccountWithPrimaryAccountHolder() {
    var element1 = checkingAccountRepository.findByIdJoined(1);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertNull(element1.get().getSecondaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnCheckingAccountWithAccountHolders() {
    var element1 = checkingAccountRepository.findByIdJoined(2);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertEquals(ah2, element1.get().getSecondaryOwner());
  }


  // ======================================== Custom Queries Testing ========================================


}
