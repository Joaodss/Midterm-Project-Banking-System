package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.MaintenanceFeeTransactionServiceImpl;
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

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MaintenanceFeeTransactionServiceTest {

  @InjectMocks
  @Spy
  private MaintenanceFeeTransactionService maintenanceFeeTransactionService = new MaintenanceFeeTransactionServiceImpl();

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
  void testNewTransaction_checkingAccount_completeNewTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    ca.setMonthlyMaintenanceFee(newMoney("12"));
    when(accountService.getById(1L)).thenReturn(ca);
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(maintenanceFeeTransactionService).validateTransaction(any());

    maintenanceFeeTransactionService.newTransaction(1L);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("12"), argumentCaptor.getValue().getBaseAmount());
    verify(maintenanceFeeTransactionService).validateTransaction(any());
    verify(accountService).updateBalance(any(Account.class));
  }

  @Test
  @Order(1)
  void testNewTransaction_SavingsAccount_throwException() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("1000"), ah);
    sa.setId(1L);
    when(accountService.getById(1L)).thenReturn(sa);

    assertThrows(IllegalArgumentException.class, () -> maintenanceFeeTransactionService.newTransaction(1L));
    verify(accountService).getById(1L);
  }

  // ==================== New Transaction for remaining balance ====================
  @Test
  @Order(2)
  void testNewTransactionOfRemaining_checkingAccount_completeNewTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("5"), ah);
    ca.setId(1L);
    ca.setMonthlyMaintenanceFee(newMoney("12"));
    when(accountService.getById(1L)).thenReturn(ca);
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(maintenanceFeeTransactionService).processTransaction(any());

    maintenanceFeeTransactionService.newTransaction(1L, newMoney("5"));

    verify(accountService).getById(1L);
    var argumentCaptor1 = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(argumentCaptor1.capture());
    assertEquals(newMoney("5"), argumentCaptor1.getValue().getBaseAmount());
    verify(maintenanceFeeTransactionService).processTransaction(any());
    var argumentCaptor2 = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor2.capture());
    assertEquals(ca, argumentCaptor2.getValue().getPersonalAccount());
    assertEquals(Status.ACCEPTED, argumentCaptor2.getValue().getStatus());
    assertEquals(newMoney("-5"), argumentCaptor2.getValue().getBaseAmount());
  }

  @Test
  @Order(2)
  void testNewTransactionOfRemaining_SavingsAccount_throwException() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("5"), ah);
    sa.setId(1L);
    when(accountService.getById(1L)).thenReturn(sa);

    assertThrows(IllegalArgumentException.class, () -> maintenanceFeeTransactionService.newTransaction(1L, newMoney("5")));
    verify(accountService).getById(1L);
  }

  // ============================== validate and process transaction Methods ==============================
  // ==================== Validate Transaction ====================
  @Test
  @Order(3)
  void testValidateTransaction_frozenAccount_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    ca.setAccountStatus(AccountStatus.FROZEN);
    var transaction = new Transaction(newMoney("10"), ca);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(true);

    maintenanceFeeTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.REFUSED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("0"), argumentCaptor.getValue().getBaseAmount());
    assertEquals("Account is frozen. Unable to withdraw maintenance fee.", argumentCaptor.getValue().getDetails());
    verify(accountService).save(any(Account.class));
  }

  @Test
  @Order(3)
  void testValidateTransaction_amountNotValid_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("1"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.SEND);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(maintenanceFeeTransactionService.isTransactionAmountValid(transaction)).thenReturn(false);
    doNothing().when(maintenanceFeeTransactionService).newTransaction(anyLong(), any(Money.class));

    maintenanceFeeTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.REFUSED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("0"), argumentCaptor.getValue().getBaseAmount());
    assertEquals("Insufficient founds to withdraw.", argumentCaptor.getValue().getDetails());
    verify(accountService).save(any(Account.class));
  }

  @Test
  @Order(3)
  void testValidateTransaction_allValid_acceptTransactionAndProcessIt() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(maintenanceFeeTransactionService.isTransactionAmountValid(transaction)).thenReturn(true);
    doNothing().when(maintenanceFeeTransactionService).processTransaction(any());

    maintenanceFeeTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.ACCEPTED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("-10"), argumentCaptor.getValue().getBaseAmount());
    verify(maintenanceFeeTransactionService).processTransaction(transaction);
    verify(accountService).save(any(Account.class));
  }


  // ==================== Process Transaction ====================
  @Test
  @Order(4)
  void testProcessTransaction_processesSendTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    ca.setLastMaintenanceFee(LocalDate.now());
    var transaction = new Transaction(newMoney("12"), ca);
    when(accountService.getById(1L)).thenReturn(ca);

    maintenanceFeeTransactionService.processTransaction(transaction);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountService).save(argumentCaptor.capture());
    var capturedSavingsAccount = (CheckingAccount) argumentCaptor.getValue();
    assertEquals(newMoney("88"), capturedSavingsAccount.getBalance());
    assertEquals(LocalDate.now().plusMonths(1), capturedSavingsAccount.getLastMaintenanceFee());
  }


  // ============================== util transaction Methods ==============================
  // ==================== Is Transaction Amount Valid ====================
  @Test
  @Order(5)
  void testIsTransactionAmountValid_validAmount_true() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("70"), ca);

    assertTrue(maintenanceFeeTransactionService.isTransactionAmountValid(transaction));
  }

  @Test
  @Order(5)
  void testIsTransactionAmountValid_invalidAmount_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("101"), ca);

    assertFalse(maintenanceFeeTransactionService.isTransactionAmountValid(transaction));
  }

}
