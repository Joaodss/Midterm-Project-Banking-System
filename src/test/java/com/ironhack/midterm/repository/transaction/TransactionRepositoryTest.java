package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.account.CheckingAccountRepository;
import com.ironhack.midterm.repository.account.CreditCardRepository;
import com.ironhack.midterm.repository.account.SavingsAccountRepository;
import com.ironhack.midterm.repository.account.StudentCheckingAccountRepository;
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
class TransactionRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private CheckingAccountRepository checkingAccountRepository;

  @Autowired
  private StudentCheckingAccountRepository studentCheckingAccountRepository;

  @Autowired
  private SavingsAccountRepository savingsAccountRepository;

  @Autowired
  private CreditCardRepository creditCardRepository;

  @Autowired
  private AccountHolderRepository accountHolderRepository;


  private AccountHolder ah1;
  private AccountHolder ah2;

  private CheckingAccount ca;
  private StudentCheckingAccount sca;
  private SavingsAccount sa;
  private CreditCard cc;

  private Transaction localTransaction;
  private Transaction thirdPartyTransaction;
  private Transaction interestTransaction;
  private Transaction maintenanceFeeTransaction;
  private Transaction penaltyFeeTransaction;


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

    localTransaction = new Transaction(newMoney("100"), sa, ca, ah2);
    thirdPartyTransaction = new Transaction(newMoney("12.98"), sca, TransactionPurpose.SEND);
    interestTransaction = new Transaction(newMoney("2.37"), cc);
    maintenanceFeeTransaction = new Transaction(newMoney("12.00"), ca);
    penaltyFeeTransaction = new Transaction(newMoney("40.00"), sa);
    transactionRepository.saveAll(List.of(localTransaction, thirdPartyTransaction, interestTransaction, maintenanceFeeTransaction, penaltyFeeTransaction));
  }

  @AfterEach
  void tearDown() throws SQLException {
    transactionRepository.deleteAll();
    checkingAccountRepository.deleteAll();
    studentCheckingAccountRepository.deleteAll();
    savingsAccountRepository.deleteAll();
    creditCardRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "account", "user", "transaction");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfTransactionsInDatabase_correctAmount() {
    assertEquals(5, transactionRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateTransaction_saveNewSimpleTransactionWithTargetAccount_storedInRepository() {
    var initialSize = transactionRepository.count();
    transactionRepository.save(new Transaction(newMoney("90"), cc));
    assertEquals(initialSize + 1, transactionRepository.count());
  }

  @Test
  @Order(2)
  void testCreateTransaction_saveNewThirdPartyTransactionWithTargetAccountAndTransactionPurpose_storedInRepository() {
    var initialSize = transactionRepository.count();
    transactionRepository.save(new Transaction(newMoney("12.98"), ca, TransactionPurpose.REQUEST));
    assertEquals(initialSize + 1, transactionRepository.count());
  }

  @Test
  @Order(2)
  void testCreateTransaction_saveNewLocalTransactionWithTargetAccountAndTargetOwner_storedInRepository() {
    var initialSize = transactionRepository.count();
    transactionRepository.save(new Transaction(newMoney("100"), cc, sa, ah2));
    assertEquals(initialSize + 1, transactionRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadTransaction_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = transactionRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadTransaction_findById_validId_returnsObjectsWithSameId() {
    var element1 = transactionRepository.findById(2L);
    assertTrue(element1.isPresent());
    assertEquals(2L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadTransaction_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = transactionRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateTransaction_changeStatus_newMinBalanceEqualsDefinedValue() {
    var element1 = transactionRepository.findById(3L);
    assertTrue(element1.isPresent());
    element1.get().setStatus(Status.REFUSED);
    transactionRepository.save(element1.get());

    var updatedElement1 = transactionRepository.findById(3L);
    assertTrue(updatedElement1.isPresent());
    assertEquals(Status.REFUSED, updatedElement1.get().getStatus());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteTransaction_deleteTransaction_validId_deletedFromRepository() {
    var initialSize = transactionRepository.count();
    transactionRepository.deleteById(4L);
    assertEquals(initialSize - 1, transactionRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteTransaction_deleteTransaction_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> transactionRepository.deleteById(99L));
  }


  // ======================================== Relations Testing ========================================
  // ==================== Read from Accounts ====================
  @Test
  @Order(6)
  void testReadFromAccounts_findByIdJoined_LocalTransaction_returnRespectiveAccountsAndTargetOwner() {
    var element1 = transactionRepository.findByIdJoined(1);
    assertFalse(element1.isEmpty());
    assertEquals(sa, element1.get().getBaseAccount());
    assertEquals(ca, element1.get().getTargetAccount());
    assertEquals(ah2, element1.get().getTargetOwner());
  }

  @Test
  @Order(6)
  void testReadFromAccounts_findByIdJoined_OtherTransaction_returnRespectiveTargetAccount() {
    var element1 = transactionRepository.findByIdJoined(2);
    assertTrue(element1.isPresent());
    assertEquals(sca, element1.get().getTargetAccount());
  }

  @Test
  @Order(6)
  void testReadFromAccounts_findByIdJoined_invalidId_returnEmpty() {
    var element1 = transactionRepository.findByIdJoined(99);
    assertTrue(element1.isEmpty());
  }

  // ======================================== Custom Queries Testing ========================================
  // ==================== Find All By Account Id Joined ====================
  @Test
  @Order(7)
  void testFindAllByAccountIdJoined_findAllByAccountIdJoined_ca_returnTransactionsRelatedFromAccount() {
    var element1 = transactionRepository.findAllByAccountIdJoined(1);
    assertFalse(element1.isEmpty());
    assertTrue(element1.contains(localTransaction));
    assertTrue(element1.contains(maintenanceFeeTransaction));
    assertFalse(element1.contains(thirdPartyTransaction));
    assertFalse(element1.contains(interestTransaction));
    assertFalse(element1.contains(penaltyFeeTransaction));
  }

  @Test
  @Order(7)
  void testFindAllByAccountIdJoined_findAllByAccountIdJoined_sa_returnTransactionsRelatedFromAccount() {
    var element1 = transactionRepository.findAllByAccountIdJoined(3);
    assertFalse(element1.isEmpty());
    assertTrue(element1.contains(localTransaction));
    assertTrue(element1.contains(penaltyFeeTransaction));
    assertFalse(element1.contains(thirdPartyTransaction));
    assertFalse(element1.contains(interestTransaction));
    assertFalse(element1.contains(maintenanceFeeTransaction));
  }

  @Test
  @Order(7)
  void testFindAllByAccountIdJoined_findAllByAccountIdJoined_invalidId_returnEmpty() {
    var element1 = transactionRepository.findAllByAccountIdJoined(99);
    assertTrue(element1.isEmpty());
  }

}
