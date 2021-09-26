package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
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
class ReceiptRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private ReceiptRepository receiptRepository;

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

  private Receipt r1s;
  private Receipt r1r;
  private Receipt r2;
  private Receipt r3;
  private Receipt r4;
  private Receipt r5;


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
    thirdPartyTransaction = new Transaction(newMoney("12.98"), sca, TransactionPurpose.REQUEST);
    interestTransaction = new Transaction(newMoney("2.37"), cc);
    maintenanceFeeTransaction = new Transaction(newMoney("12.00"), ca);
    penaltyFeeTransaction = new Transaction(newMoney("40.00"), sa);
    transactionRepository.saveAll(List.of(localTransaction, thirdPartyTransaction, interestTransaction, maintenanceFeeTransaction, penaltyFeeTransaction));

    r1s = localTransaction.generateLocalTransactionSenderReceipt(true);
    r1r = localTransaction.generateLocalTransactionReceiverReceipt(true);
    r2 = thirdPartyTransaction.generateThirdPartyTransactionReceipt(false);
    r3 = interestTransaction.generateInterestTransactionReceipt(true);
    r4 = maintenanceFeeTransaction.generateMaintenanceFeeTransactionReceipt(true);
    r5 = penaltyFeeTransaction.generatePenaltyFeeTransactionReceipt(false, "Transaction error.");
    receiptRepository.saveAll(List.of(r1s, r1r, r2, r3, r4, r5));
  }

  @AfterEach
  void tearDown() throws SQLException {
    transactionRepository.deleteAll();
    checkingAccountRepository.deleteAll();
    studentCheckingAccountRepository.deleteAll();
    savingsAccountRepository.deleteAll();
    creditCardRepository.deleteAll();
    accountHolderRepository.deleteAll();
    DbResetUtil.resetAutoIncrementColumns(applicationContext, "account", "user", "transaction", "receipt");
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfReceiptsInDatabase_correctAmount() {
    assertEquals(6, receiptRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateReceipt_saveNewSimpleReceiptWithTargetAccount_storedInRepository() {
    var initialSize = receiptRepository.count();
    var newTransaction = transactionRepository.save(new Transaction(newMoney("2.37"), cc));
    transactionRepository.save(newTransaction);
    receiptRepository.save(newTransaction.generateInterestTransactionReceipt(true));
    assertEquals(initialSize + 1, receiptRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadReceipt_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = receiptRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadReceipt_findById_validId_returnsObjectsWithSameId() {
    var element1 = receiptRepository.findById(3L);
    assertTrue(element1.isPresent());
    assertEquals(3L, element1.get().getId());
  }

  @Test
  @Order(3)
  void testReadReceipt_findById_invalidId_returnsObjectsWithSameId() {
    var element1 = receiptRepository.findById(99L);
    assertTrue(element1.isEmpty());
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateReceipt_changeDetails_newMinBalanceEqualsDefinedValue() {
    var element1 = receiptRepository.findById(5L);
    assertTrue(element1.isPresent());
    element1.get().setDetails("New description");
    receiptRepository.save(element1.get());

    var updatedElement1 = receiptRepository.findById(5L);
    assertTrue(updatedElement1.isPresent());
    assertEquals("New description", updatedElement1.get().getDetails());
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteReceipt_deleteReceipt_validId_deletedFromRepository() {
    var initialSize = receiptRepository.count();
    receiptRepository.deleteById(4L);
    assertEquals(initialSize - 1, receiptRepository.count());
  }

  @Test
  @Order(5)
  void testDeleteReceipt_deleteReceipt_invalidId_deletedFromRepository() {
    assertThrows(EmptyResultDataAccessException.class, () -> receiptRepository.deleteById(99L));
  }

  // ======================================== Relations Testing ========================================
  // ==================== Read from Accounts ====================
  @Test
  @Order(6)
  void testReadFromAccounts_findByIdJoined_receiptWithInternalAndExternalAccounts_returnRespectiveAccounts() {
    var element1 = receiptRepository.findByIdJoined(1);
    assertTrue(element1.isPresent());
    assertEquals(sa, element1.get().getPersonalAccount());
    assertEquals(ca, element1.get().getExternalAccount());
  }

  @Test
  @Order(6)
  void testReadFromAccounts_findByIdJoined_receiptWithInternalAccount_returnRespectiveAccount() {
    var element1 = receiptRepository.findByIdJoined(3);
    assertTrue(element1.isPresent());
    assertEquals(sca, element1.get().getPersonalAccount());
    assertNull(element1.get().getExternalAccount());
  }

  @Test
  @Order(6)
  void testReadFromAccounts_findByIdJoined_invalidId_returnEmpty() {
    var element1 = receiptRepository.findByIdJoined(99);
    assertTrue(element1.isEmpty());
  }


  // ======================================== Custom Query Testing ========================================
  // ==================== Find By Transaction Id Joined ====================
  @Test
  @Order(7)
  void testFindByTransactionIdJoined_findByTransactionIdJoined_validIds_returnReceipt() {
    var element1 = receiptRepository.findByTransactionIdJoined(1L, 1L);
    assertTrue(element1.isPresent());
    assertEquals(r1r, element1.get());
  }

  @Test
  @Order(7)
  void testFindByTransactionIdJoined_findByTransactionIdJoined_invalidIdMatch_returnEmpty() {
    var element1 = receiptRepository.findByTransactionIdJoined(2L, 1L);
    assertTrue(element1.isEmpty());
  }

}