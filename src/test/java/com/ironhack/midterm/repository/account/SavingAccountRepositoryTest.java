package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.util.database.DbResetUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SavingAccountRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private SavingsAccountRepository savingsAccountRepository;


  private AccountHolder ah1;
  private AccountHolder ah2;

  private SavingsAccount sa1;
  private SavingsAccount sa2;
  private SavingsAccount sa3;


  @BeforeEach
  void setUp() {
    var pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    var pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");
    ah1 = new AccountHolder("joa0ds5", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa1);
    ah2 = new AccountHolder("an5m6ri7", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    accountHolderRepository.saveAll(List.of(ah1, ah2));

    sa1 = new SavingsAccount(newMoney("2000"), ah1, "abcdef123");
    sa2 = new SavingsAccount(newMoney("500"), ah1, ah2, "secretword");
    sa3 = new SavingsAccount(newMoney("1000"), ah2, "password123");
    savingsAccountRepository.saveAll(List.of(sa1, sa2, sa3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    savingsAccountRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "account", "user");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfSavingsAccountsInDatabase_correctAmount() {
    assertEquals(3, savingsAccountRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateSavingsAccount_saveNewSavingsAccountWithOneOwner_storedInRepository() {
    var initialSize = savingsAccountRepository.count();
    savingsAccountRepository.save(new SavingsAccount(newMoney("2500"), ah1, "testTest"));
    assertEquals(initialSize + 1, savingsAccountRepository.count());
  }

  @Test
  @Order(2)
  void testCreateSavingsAccount_saveNewSavingsAccountWithTwoOwner_storedInRepository() {
    var initialSize = savingsAccountRepository.count();
    savingsAccountRepository.save(new SavingsAccount(newMoney("7000"), ah1, ah2, "testTest2"));
    assertEquals(initialSize + 1, savingsAccountRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadSavingsAccount_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = savingsAccountRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadSavingsAccount_findById_validId_returnsObjectsWithSameId() {
    var element1 = savingsAccountRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadSavingsAccount_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = savingsAccountRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateSavingsAccount_changeBalance_newMinBalanceEqualsDefinedValue() {
    var element1 = savingsAccountRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setBalance(newMoney("9020"));
    savingsAccountRepository.save(element1.get());

    var updatedElement1 = savingsAccountRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals(newMoney("9020"), updatedElement1.get().getBalance());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteSavingsAccount_deleteSavingsAccount_validId_deletedFromRepository() {
    var initialSize = savingsAccountRepository.count();
    savingsAccountRepository.deleteById(2L);
    assertEquals(initialSize - 1, savingsAccountRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteSavingsAccount_deleteSavingsAccount_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> savingsAccountRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from AccountHolders ====================
  @Test
  @Order(6)
  void testReadFromAccountHolders_findAllJoined_returnSavingsAccountsWithAccountHolders() {
    var element1 = savingsAccountRepository.findAllJoined();
    assertFalse(element1.isEmpty());
    assertEquals(ah1, element1.get(0).getPrimaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnSavingsAccountWithPrimaryAccountHolder() {
    var element1 = savingsAccountRepository.findByIdJoined(1);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertNull(element1.get().getSecondaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnSavingsAccountWithAccountHolders() {
    var element1 = savingsAccountRepository.findByIdJoined(2);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertEquals(ah2, element1.get().getSecondaryOwner());
  }


  // ======================================== Custom Queries Testing ========================================


}
