package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.utils.DbResetUtil;
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
class AccountRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CheckingAccountRepository checkingAccountRepository;

  @Autowired
  private StudentCheckingAccountRepository studentCheckingAccountRepository;

  @Autowired
  private SavingsAccountRepository savingsAccountRepository;

  @Autowired
  private CreditCardRepository creditCardRepository;

  private AccountHolder ah1;
  private AccountHolder ah2;

  private CheckingAccount ca;
  private StudentCheckingAccount sca;
  private SavingsAccount sa;
  private CreditCard cc;

  @BeforeEach
  void setUp() throws NoSuchAlgorithmException {
    var pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    var pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");
    ah1 = new AccountHolder("joa0ds5", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa1);
    ah2 = new AccountHolder("an5m6ri7", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    accountHolderRepository.saveAll(List.of(ah1, ah2));

    ca = new CheckingAccount(newMoney("2000"), ah2);
    checkingAccountRepository.save(ca);
    sca = new StudentCheckingAccount(newMoney("500"), ah1);
    studentCheckingAccountRepository.save(sca);
    sa = new SavingsAccount(newMoney("15681"), ah2);
    savingsAccountRepository.save(sa);
    cc = new CreditCard(newMoney("1000"), ah1);
    creditCardRepository.save(cc);
  }

  @AfterEach
  void tearDown() throws SQLException {
    checkingAccountRepository.deleteAll();
    studentCheckingAccountRepository.deleteAll();
    savingsAccountRepository.deleteAll();
    creditCardRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "account","user");
  }

  // ======================================== READ and DELETE TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfAccountsInDatabase_correctAmount() {
    assertEquals(4, accountRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(2)
  void testReadAccounts_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = accountRepository.findAll();
    assertFalse(allElements.isEmpty());
    assertEquals(4, allElements.size());
  }

  @Test
  @Order(2)
  void testReadAccount_findById_validId_returnsObjectsWithSameId() {
    var element1 = accountRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(2)
  void testReadAccount_findById_invalidId_returnsEmpty() {
    var element1 = accountRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Custom Read ====================
  @Test
  @Order(3)
  void testReadAccount_findByAccountUsername_validIdAccountHolder_returnsCorrectObject() {
    var element1 = accountRepository.findAllByUsernameJoined("joa0ds5");
    assertFalse(element1.isEmpty());
    assertTrue(element1.contains(sca));
    assertTrue(element1.contains(cc));
    assertFalse(element1.contains(ca));
    assertFalse(element1.contains(sa));
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteAccount_deleteThirdParty_validId_deletedFromRepository() {
    var initialSize = accountRepository.count();
    accountRepository.deleteById(2L);
    assertEquals(initialSize - 1, accountRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteAccount_deleteAccount_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> accountRepository.deleteById(99L));
  }

}