package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.TransactionServiceImpl;
import com.ironhack.midterm.util.DateTimeUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @InjectMocks
  @Spy
  private TransactionService transactionService = new TransactionServiceImpl();

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private AccountService accountService;


  // ======================================== get Methods ========================================
  // ==================== Get All By Account Id ====================
  @Test
  @Order(1)
  void testGetAllByAccountId_validAccountId_checkIfExistsWithAccountService_useTransactionRepositoryFindAllByAccountIdJoined() {
    when(accountService.hasAccount(1L)).thenReturn(true);

    transactionService.getAllByAccountId(1L);

    verify(accountService).hasAccount(1L);
    verify(transactionRepository).findAllByAccountIdJoined(1L);
    verifyNoMoreInteractions(accountService);
    verifyNoMoreInteractions(transactionRepository);
  }

  @Test
  @Order(1)
  void testGetAllByAccountId_invalidAccountId_throwException() {
    when(accountService.hasAccount(99L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> transactionService.getAllByAccountId(99L));
    verify(accountService).hasAccount(99L);
    verifyNoInteractions(transactionRepository);
  }


  // ==================== Get By Id ====================
  @Test
  @Order(2)
  void testGetById_validAccountIdAndValidTransactionId_checkIfExistsWithAccountService_useTransactionRepositoryFindAllByAccountIdJoined() {
    var account = new CheckingAccount();
    account.setId(1L);
    when(accountService.hasAccount(1L)).thenReturn(true);

    var transaction = new Transaction();
    transaction.setTargetAccount(account);
    when(transactionRepository.findByIdJoined(1L)).thenReturn(Optional.of(transaction));

    assertEquals(transaction, transactionService.getById(1L, 1L));

    verify(accountService).hasAccount(1L);
    verify(transactionRepository).findByIdJoined(1L);
    verifyNoMoreInteractions(accountService);
    verifyNoMoreInteractions(transactionRepository);
  }

  @Test
  @Order(2)
  void testGetById_invalidAccountId_throwException() {
    when(accountService.hasAccount(99L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> transactionService.getById(99L, 1L));

    verify(accountService).hasAccount(99L);
    verifyNoMoreInteractions(accountService);
    verifyNoInteractions(transactionRepository);
  }

  @Test
  @Order(2)
  void testGetById_invalidTransactionId_throwException() {
    when(accountService.hasAccount(1L)).thenReturn(true);
    when(transactionRepository.findByIdJoined(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> transactionService.getById(1L, 99L));

    verify(accountService).hasAccount(1L);
    verify(transactionRepository).findByIdJoined(99L);
    verifyNoMoreInteractions(accountService);
    verifyNoMoreInteractions(transactionRepository);
  }

  @Test
  @Order(2)
  void testGetById_idDoNotMatch_throwException() {
    var account = new CheckingAccount();
    account.setId(1L);
    var account2 = new CheckingAccount();
    account2.setId(2L);
    when(accountService.hasAccount(1L)).thenReturn(true);

    var transaction = new Transaction();
    transaction.setTargetAccount(account2);
    when(transactionRepository.findByIdJoined(1L)).thenReturn(Optional.of(transaction));

    assertThrows(IllegalArgumentException.class, () -> transactionService.getById(1L, 1L));

    verify(accountService).hasAccount(1L);
    verify(transactionRepository).findByIdJoined(1L);
    verifyNoMoreInteractions(accountService);
    verifyNoMoreInteractions(transactionRepository);
  }


  // ======================================== utils Methods ========================================
  // ==================== Get By Id ====================
  @Test
  @Order(3)
  void testIsAccountFrozen_checkingAccounts_ActiveAccounts_false() {
    var account = new CheckingAccount();
    account.setAccountStatus(AccountStatus.ACTIVE);
    var account2 = new CheckingAccount();
    account2.setAccountStatus(AccountStatus.ACTIVE);
    var transaction = new Transaction();
    transaction.setTargetAccount(account);
    transaction.setBaseAccount(account2);

    assertFalse(transactionService.isAccountFrozen(transaction));
  }

  @Test
  @Order(3)
  void testIsAccountFrozen_studentCheckingAccounts_FrozenBaseAccount_true() {
    var account = new StudentCheckingAccount();
    account.setAccountStatus(AccountStatus.ACTIVE);
    var account2 = new StudentCheckingAccount();
    account2.setAccountStatus(AccountStatus.FROZEN);
    var transaction = new Transaction();
    transaction.setTargetAccount(account);
    transaction.setBaseAccount(account2);

    assertTrue(transactionService.isAccountFrozen(transaction));
  }

  @Test
  @Order(3)
  void testIsAccountFrozen_savingsAccount_activeAccounts_false() {
    var account = new SavingsAccount();
    account.setAccountStatus(AccountStatus.ACTIVE);
    var account2 = new SavingsAccount();
    account2.setAccountStatus(AccountStatus.ACTIVE);
    var transaction = new Transaction();
    transaction.setTargetAccount(account);
    transaction.setBaseAccount(account2);
    assertFalse(transactionService.isAccountFrozen(transaction));
  }

  @Test
  @Order(3)
  void testIsAccountFrozen_savingsAccount_activeTargetAccount_false() {
    var account = new SavingsAccount();
    account.setAccountStatus(AccountStatus.ACTIVE);
    var transaction = new Transaction();
    transaction.setTargetAccount(account);

    assertFalse(transactionService.isAccountFrozen(transaction));
  }


  // ======================================== fraud detection Methods ========================================
  // ==================== Is Transaction Time Fraudulent ====================
  @Test
  @Order(4)
  void testIsTransactionTimeFraudulent_firstTransaction_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sa = new SavingsAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sa.setId(1L);
    var transaction = new Transaction(newMoney("100"), sa);

    assertFalse(transactionService.isTransactionTimeFraudulent(transaction.getTargetAccount()));
  }

  @Test
  @Order(4)
  void testIsTransactionTimeFraudulent_transactionsMoreThanOneSecondApart_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sa = new SavingsAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sa.setId(1L);
    var transaction1 = new Transaction(newMoney("100"), sa);
    var transaction2 = new Transaction(newMoney("200"), sa);
    transaction1.setOperationDate(DateTimeUtil.dateTimeNow());
    transaction2.setOperationDate(transaction1.getOperationDate().plusSeconds(4));
    sa.setIncomingTransactions(List.of(transaction1, transaction2));

    assertFalse(transactionService.isTransactionTimeFraudulent(transaction2.getTargetAccount()));
  }

  @Test
  @Order(4)
  void testIsTransactionTimeFraudulent_transactionsLessThanOneSecondApart_true() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sa = new SavingsAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sa.setId(1L);
    var transaction1 = new Transaction(newMoney("100"), sa);
    var transaction2 = new Transaction(newMoney("200"), sa);
    transaction1.setOperationDate(DateTimeUtil.dateTimeNow());
    transaction2.setOperationDate(transaction1.getOperationDate().plusNanos(20));
    sa.setIncomingTransactions(List.of(transaction1, transaction2));

    assertTrue(transactionService.isTransactionTimeFraudulent(transaction2.getTargetAccount()));
  }

  // ==================== Is Transaction Daily Amount Fraudulent ====================
  @Test
  @Order(5)
  void testIsTransactionDailyAmountFraudulent_lowerLastDayAmountThanTheRequired_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sca = new StudentCheckingAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sca.setId(1L);
    when(transactionService.lastDayTransactions(sca)).thenReturn(newMoney("4500"));
    when(transactionService.allDailyMax(sca)).thenReturn(newMoney("4000"));

    assertFalse(transactionService.isTransactionDailyAmountFraudulent(sca));
  }

  @Test
  @Order(5)
  void testIsTransactionDailyAmountFraudulent_lowerLastDayAmountThanTheBaseRequired_higherThanMaxDailyRequired_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sca = new StudentCheckingAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sca.setId(1L);
    when(transactionService.lastDayTransactions(sca)).thenReturn(newMoney("900")); // < 1000
    when(transactionService.allDailyMax(sca)).thenReturn(newMoney("30"));

    assertFalse(transactionService.isTransactionDailyAmountFraudulent(sca));
  }

  @Test
  @Order(5)
  void testIsTransactionDailyAmountFraudulent_HigherThanMaxDailyRequired_andBaseRequired_true() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sca = new StudentCheckingAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sca.setId(1L);
    when(transactionService.lastDayTransactions(sca)).thenReturn(newMoney("1010")); // < 1000
    when(transactionService.allDailyMax(sca)).thenReturn(newMoney("600"));

    assertTrue(transactionService.isTransactionDailyAmountFraudulent(sca));
  }


  // ======================================== utils of fraud detection ========================================
  // ==================== Is Transaction Daily Amount Fraudulent ====================
  @Test
  @Order(6)
  void testLastDayTransactions() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sca = new StudentCheckingAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sca.setId(1L);

    var dailyTransactionsMap = new HashMap<LocalDate, Money>();
    dailyTransactionsMap.put(LocalDate.now(), newMoney("100"));
    dailyTransactionsMap.put(LocalDate.now().minusDays(6), newMoney("800"));
    dailyTransactionsMap.put(LocalDate.now().minusDays(8), newMoney("35"));
    dailyTransactionsMap.put(LocalDate.now().minusDays(15), newMoney("472"));
    when(transactionService.dailyTransactions(sca)).thenReturn(dailyTransactionsMap);

    var result = transactionService.lastDayTransactions(sca);

    verify(transactionService).lastDayTransactions(sca);
    assertEquals(newMoney("100"), result);
  }

  // ==================== Is Transaction Daily Amount Fraudulent ====================
  @Test
  @Order(7)
  void testAllDailyMax() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var sca = new StudentCheckingAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    sca.setId(1L);

    var dailyTransactionsMap = new HashMap<LocalDate, Money>();
    dailyTransactionsMap.put(LocalDate.now(), newMoney("100"));
    dailyTransactionsMap.put(LocalDate.now().minusDays(6), newMoney("800"));
    dailyTransactionsMap.put(LocalDate.now().minusDays(8), newMoney("35"));
    dailyTransactionsMap.put(LocalDate.now().minusDays(15), newMoney("472"));
    when(transactionService.dailyTransactions(sca)).thenReturn(dailyTransactionsMap);

    var result = transactionService.allDailyMax(sca);

    verify(transactionService).dailyTransactions(sca);
    assertEquals(newMoney("800"), result);
  }

  // ==================== Is Transaction Daily Amount Fraudulent ====================
  @Test
  @Order(8)
  void testDailyTransactions() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new CheckingAccount(newMoney("7000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    ca.setId(1L);
    var t1 = new Transaction(newMoney("100"), ca);
    t1.setOperationDate(DateTimeUtil.dateTimeNow());
    var t2 = new Transaction(newMoney("100"), ca);
    t2.setOperationDate(t1.getOperationDate().minusDays(1));
    var t3 = new Transaction(newMoney("100"), ca);
    t3.setOperationDate(t1.getOperationDate().minusDays(1));
    var t4 = new Transaction(newMoney("250"), ca);
    t4.setOperationDate(t1.getOperationDate().minusDays(3));
    var t5 = new Transaction(newMoney("400"), ca);
    t5.setOperationDate(t1.getOperationDate().minusDays(5));
    var t6 = new Transaction(newMoney("400"), ca);
    t6.setOperationDate(t1.getOperationDate().minusDays(5));
    ca.setIncomingTransactions(List.of(t1, t2, t3, t4, t5, t6));

    var result = transactionService.dailyTransactions(ca);

    assertFalse(result.isEmpty());
    assertEquals(4, result.size());
    assertEquals(newMoney("100"), result.get(LocalDate.now()));
    assertEquals(newMoney("200") /* 100+100 */, result.get(LocalDate.now().minusDays(1)));
    assertEquals(newMoney("250") /* 250 */, result.get(LocalDate.now().minusDays(3)));
    assertEquals(newMoney("800") /* 400+400 */, result.get(LocalDate.now().minusDays(5)));
  }

}