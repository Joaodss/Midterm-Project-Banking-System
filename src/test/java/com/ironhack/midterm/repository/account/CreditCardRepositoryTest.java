package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.util.DbTestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.MoneyHelper.newBD;
import static com.ironhack.midterm.util.MoneyHelper.newMoney;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CreditCardRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private CreditCardRepository creditCardRepository;


  private AccountHolder ah1;
  private AccountHolder ah2;

  private CreditCard cc1;
  private CreditCard cc2;
  private CreditCard cc3;


  @BeforeEach
  void setUp() {
    var pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    var pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");
    ah1 = new AccountHolder("joa0ds5", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa1);
    ah2 = new AccountHolder("an5m6ri7", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    accountHolderRepository.saveAll(List.of(ah1, ah2));

    cc1 = new CreditCard(newMoney("2000"), ah1);
    cc2 = new CreditCard(newMoney("500"), ah1, ah2);
    cc3 = new CreditCard(newMoney("1000"), ah2);
    creditCardRepository.saveAll(List.of(cc1, cc2, cc3));
  }

  @AfterEach
  void tearDown() throws SQLException {
    creditCardRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbTestUtil.resetAutoIncrementColumns(applicationContext, "account", "user");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfCreditCardsInDatabase_correctAmount() {
    assertEquals(3, creditCardRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateCreditCard_saveNewCreditCardWithOneOwner_storedInRepository() {
    var initialSize = creditCardRepository.count();
    creditCardRepository.save(new CreditCard(newMoney("2500"), ah1));
    assertEquals(initialSize + 1, creditCardRepository.count());
  }

  @Test
  @Order(2)
  void testCreateCreditCard_saveNewCreditCardWithTwoOwner_storedInRepository() {
    var initialSize = creditCardRepository.count();
    creditCardRepository.save(new CreditCard(newMoney("7000"), ah1, ah2));
    assertEquals(initialSize + 1, creditCardRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadCreditCard_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = creditCardRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadCreditCard_findById_validId_returnsObjectsWithSameId() {
    var element1 = creditCardRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadCreditCard_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = creditCardRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateCreditCard_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = creditCardRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setInterestRate(newBD("0.12"));
    creditCardRepository.save(element1.get());

    var updatedElement1 = creditCardRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals(newBD("0.12", 4), updatedElement1.get().getInterestRate());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteCreditCard_deleteCreditCard_validId_deletedFromRepository() {
    var initialSize = creditCardRepository.count();
    creditCardRepository.deleteById(2L);
    assertEquals(initialSize - 1, creditCardRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteCreditCard_deleteCreditCard_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> creditCardRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from AccountHolders ====================
  @Test
  @Order(6)
  void testReadFromAccountHolders_findAllJoined_returnCreditCardsWithAccountHolders() {
    var element1 = creditCardRepository.findAllJoined();
    assertFalse(element1.isEmpty());
    assertEquals(ah1, element1.get(0).getPrimaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnCreditCardWithPrimaryAccountHolder() {
    var element1 = creditCardRepository.findByIdJoined(1);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertNull(element1.get().getSecondaryOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccountHolders_findByIdJoined_returnCreditCardWithAccountHolders() {
    var element1 = creditCardRepository.findByIdJoined(2);
    assertTrue(element1.isPresent());
    assertEquals(ah1, element1.get().getPrimaryOwner());
    assertEquals(ah2, element1.get().getSecondaryOwner());
  }


  // ======================================== Custom Queries Testing ========================================


}
