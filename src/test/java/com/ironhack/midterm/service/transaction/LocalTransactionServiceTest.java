package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.TransactionLocalDTO;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.LocalTransactionServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class LocalTransactionServiceTest {

  @InjectMocks
  @Spy
  private LocalTransactionService localTransactionService = new LocalTransactionServiceImpl();

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private ReceiptRepository receiptRepository;

  @Mock
  private AccountService accountService;

  @Mock
  private TransactionService transactionService;


  // ======================================== new Methods ========================================
  // ==================== New Transaction ====================
  @Test
  @Order(1)
  void testNewTransaction_validTargetOwners_completeNewTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var dto = new TransactionLocalDTO(new BigDecimal("10"), "EUR", 2L, "João");
    when(accountService.getById(1L)).thenReturn(ca);
    when(accountService.getById(2L)).thenReturn(sa);
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(localTransactionService).validateTransaction(any());

    localTransactionService.newTransaction(1L, dto);

    verify(accountService).getById(1L);
    verify(accountService).getById(2L);
    var argumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("10"), argumentCaptor.getValue().getBaseAmount());
    verify(localTransactionService).validateTransaction(any());
    verify(accountService, times(2)).updateBalance(any(Account.class));
  }

  @Test
  @Order(1)
  void testNewTransaction_invalidTargetOwners_throwException() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var dto = new TransactionLocalDTO(new BigDecimal("10"), "EUR", 2L, "Ana");
    when(accountService.getById(1L)).thenReturn(ca);
    when(accountService.getById(2L)).thenReturn(sa);

    assertThrows(IllegalArgumentException.class, () -> localTransactionService.newTransaction(1L, dto));
    verify(accountService).getById(1L);
    verify(accountService).getById(2L);
  }


  // ============================== validate and process transaction Methods ==============================
  // ==================== Validate Transaction ====================
  @Test
  @Order(2)
  void testValidateTransaction_frozenAccount_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    ca.setAccountStatus(AccountStatus.FROZEN);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("10"), sa, ca, ah);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(true);

    localTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository, times(2)).save(argumentCaptor.capture());
    var results = argumentCaptor.getAllValues();

    assertEquals(ca, results.get(0).getPersonalAccount());
    assertEquals(Status.REFUSED, results.get(0).getStatus());
    assertEquals(newMoney("0"), results.get(0).getBaseAmount());
    assertEquals("Unable to complete the transaction. One of the accounts involved in the transaction is unavailable (frozen).", results.get(0).getDetails());

    assertEquals(sa, results.get(1).getPersonalAccount());
    assertEquals(Status.REFUSED, results.get(1).getStatus());
    assertEquals(newMoney("0"), results.get(1).getBaseAmount());
    assertEquals("Unable to complete the transaction. One of the accounts involved in the transaction is unavailable (frozen).", results.get(1).getDetails());

    verify(accountService, times(2)).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_fraudulentTransaction_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("10"), sa, ca, ah);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(transactionService.isTransactionTimeFraudulent(transaction.getBaseAccount())).thenReturn(true);

    localTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository, times(2)).save(argumentCaptor.capture());
    var results = argumentCaptor.getAllValues();

    assertEquals(ca, results.get(0).getPersonalAccount());
    assertEquals(Status.REFUSED, results.get(0).getStatus());
    assertEquals(newMoney("0"), results.get(0).getBaseAmount());

    assertEquals(sa, results.get(1).getPersonalAccount());
    assertEquals(Status.REFUSED, results.get(1).getStatus());
    assertEquals(newMoney("0"), results.get(1).getBaseAmount());
    assertEquals("Fraudulent behaviour detected! Base account was frozen.", results.get(1).getDetails());

    verify(accountService, times(2)).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_amountNotValid_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("10"), sa, ca, ah);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(transactionService.isTransactionTimeFraudulent(transaction.getBaseAccount())).thenReturn(false);
    when(localTransactionService.isTransactionAmountValid(transaction)).thenReturn(false);

    localTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository, times(2)).save(argumentCaptor.capture());
    var results = argumentCaptor.getAllValues();

    assertEquals(ca, results.get(0).getPersonalAccount());
    assertEquals(Status.REFUSED, results.get(0).getStatus());
    assertEquals(newMoney("0"), results.get(0).getBaseAmount());

    assertEquals(sa, results.get(1).getPersonalAccount());
    assertEquals(Status.REFUSED, results.get(1).getStatus());
    assertEquals(newMoney("0"), results.get(1).getBaseAmount());
    assertEquals("Invalid amount to transfer.", results.get(1).getDetails());

    verify(accountService, times(2)).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_allValid_acceptTransactionAndProcessIt() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("10"), sa, ca, ah);
    transaction.setId(1L);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(transactionService.isTransactionTimeFraudulent(transaction.getBaseAccount())).thenReturn(false);
    when(localTransactionService.isTransactionAmountValid(transaction)).thenReturn(true);
    doNothing().when(localTransactionService).processTransaction(transaction);

    localTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository, times(2)).save(argumentCaptor.capture());
    var results = argumentCaptor.getAllValues();

    assertEquals(ca, results.get(0).getPersonalAccount());
    assertEquals(Status.ACCEPTED, results.get(0).getStatus());
    assertEquals(newMoney("10"), results.get(0).getBaseAmount());

    assertEquals(sa, results.get(1).getPersonalAccount());
    assertEquals(Status.ACCEPTED, results.get(1).getStatus());
    assertEquals(newMoney("-10"), results.get(1).getBaseAmount());
    verify(localTransactionService).processTransaction(transaction);
    verify(accountService, times(2)).save(any(Account.class));
  }


  // ==================== Process Transaction ====================
  @Test
  @Order(3)
  void testProcessTransaction_processes_transaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("10"), sa, ca, ah);
    when(accountService.getById(1L)).thenReturn(ca);
    when(accountService.getById(2L)).thenReturn(sa);

    localTransactionService.processTransaction(transaction);

    verify(accountService).getById(1L);
    verify(accountService).getById(2L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountService, times(2)).save(argumentCaptor.capture());
    var result = argumentCaptor.getAllValues();
    assertEquals(newMoney("369"), result.get(0).getBalance());
    assertEquals(newMoney("110"), result.get(1).getBalance());
  }

  // ============================== util transaction Methods ==============================
  // ==================== Is Transaction Amount Valid ====================
  @Test
  @Order(4)
  void testIsTransactionAmountValid_validAmount_true() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("70"), ca, sa, ah);

    assertTrue(localTransactionService.isTransactionAmountValid(transaction));
  }

  @Test
  @Order(4)
  void testIsTransactionAmountValid_invalidAmount_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var sa = new SavingsAccount(newMoney("379"), ah);
    sa.setId(2L);
    var transaction = new Transaction(newMoney("101"), ca, sa, ah);

    assertFalse(localTransactionService.isTransactionAmountValid(transaction));
  }

}
