package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.TransactionThirdPartyDTO;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.ThirdPartyTransactionServiceImpl;
import com.ironhack.midterm.service.user.ThirdPartyService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ThirdPartyTransactionServiceTest {

  @InjectMocks
  @Spy
  private ThirdPartyTransactionService thirdPartyTransactionService = new ThirdPartyTransactionServiceImpl();

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private ReceiptRepository receiptRepository;

  @Mock
  private AccountService accountService;

  @Mock
  private TransactionService transactionService;

  @Mock
  private ThirdPartyService thirdPartyService;


  // ======================================== new Methods ========================================
  // ==================== New Transaction ====================
  @Test
  @Order(1)
  void testNewTransaction_validTargetOwners_completeNewTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    ca.setSecretKey("secretKey");
    var transactionDTO = new TransactionThirdPartyDTO(new BigDecimal("10"), "EUR", 1L, "secretKey", "SEND");
    when(thirdPartyService.hasHashedKey("hashedKeyHere")).thenReturn(true);
    when(accountService.getById(1L)).thenReturn(ca);
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(thirdPartyTransactionService).validateTransaction(any());

    thirdPartyTransactionService.newTransaction("hashedKeyHere", transactionDTO);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("10"), argumentCaptor.getValue().getBaseAmount());
    assertEquals(TransactionPurpose.SEND, argumentCaptor.getValue().getTransactionPurpose());
    verify(thirdPartyTransactionService).validateTransaction(any());
    verify(accountService).updateBalance(any(Account.class));
  }

  @Test
  @Order(1)
  void testNewTransaction_invalidHashedKey_throwException() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    ca.setSecretKey("secretKey");
    var transactionDTO = new TransactionThirdPartyDTO(new BigDecimal("10"), "EUR", 1L, "secretKey", "SEND");
    when(thirdPartyService.hasHashedKey("hashedKeyHere")).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> thirdPartyTransactionService.newTransaction("hashedKeyHere", transactionDTO));
    verify(thirdPartyService).hasHashedKey("hashedKeyHere");
  }

  @Test
  @Order(1)
  void testNewTransaction_invalidAccountkey_throwException() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transactionDTO = new TransactionThirdPartyDTO(new BigDecimal("10"), "EUR", 1L, "otherSecretKey", "SEND");
    when(thirdPartyService.hasHashedKey("hashedKeyHere")).thenReturn(true);
    when(accountService.getById(1L)).thenReturn(ca);

    assertThrows(IllegalArgumentException.class, () -> thirdPartyTransactionService.newTransaction("hashedKeyHere", transactionDTO));
    verify(thirdPartyService).hasHashedKey("hashedKeyHere");
    verify(accountService).getById(1L);
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
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.SEND);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(true);

    thirdPartyTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.REFUSED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("0"), argumentCaptor.getValue().getBaseAmount());
    assertEquals("Account is frozen. Unable to complete the transaction.", argumentCaptor.getValue().getDetails());
    verify(accountService).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_fraudulentTransaction_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.REQUEST);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(transactionService.isTransactionDailyAmountFraudulent(transaction.getTargetAccount())).thenReturn(true);

    thirdPartyTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.REFUSED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("0"), argumentCaptor.getValue().getBaseAmount());
    assertEquals("Fraudulent behaviour detected! Base account was frozen for safety.", argumentCaptor.getValue().getDetails());
    verify(accountService).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_amountNotValid_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.SEND);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(thirdPartyTransactionService.isTransactionAmountValid(transaction)).thenReturn(false);

    thirdPartyTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.REFUSED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("0"), argumentCaptor.getValue().getBaseAmount());
    assertEquals("Invalid amount to transfer.", argumentCaptor.getValue().getDetails());
    verify(accountService).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_allValid_acceptTransactionAndProcessIt() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.REQUEST);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    when(transactionService.isTransactionDailyAmountFraudulent(transaction.getTargetAccount())).thenReturn(false);
    when(thirdPartyTransactionService.isTransactionAmountValid(transaction)).thenReturn(true);
    doNothing().when(thirdPartyTransactionService).processTransaction(transaction);

    thirdPartyTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(ca, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.ACCEPTED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("-10"), argumentCaptor.getValue().getBaseAmount());
    verify(thirdPartyTransactionService).processTransaction(transaction);
    verify(accountService).save(any(Account.class));
  }


  // ==================== Process Transaction ====================
  @Test
  @Order(3)
  void testProcessTransaction_processesSendTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.SEND);
    when(accountService.getById(1L)).thenReturn(ca);

    thirdPartyTransactionService.processTransaction(transaction);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountService).save(argumentCaptor.capture());
    assertEquals(newMoney("110"), argumentCaptor.getValue().getBalance());
  }


  @Test
  @Order(3)
  void testProcessTransaction_processesRequestTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("10"), ca, TransactionPurpose.REQUEST);
    when(accountService.getById(1L)).thenReturn(ca);

    thirdPartyTransactionService.processTransaction(transaction);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountService).save(argumentCaptor.capture());
    assertEquals(newMoney("90"), argumentCaptor.getValue().getBalance());
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
    var transaction = new Transaction(newMoney("70"), ca, TransactionPurpose.REQUEST);

    assertTrue(thirdPartyTransactionService.isTransactionAmountValid(transaction));
  }

  @Test
  @Order(4)
  void testIsTransactionAmountValid_invalidAmount_false() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("101"), ca, TransactionPurpose.REQUEST);

    assertFalse(thirdPartyTransactionService.isTransactionAmountValid(transaction));
  }

  @Test
  @Order(4)
  void testIsTransactionAmountValid_sendPurpose_true() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("100"), ah);
    ca.setId(1L);
    var transaction = new Transaction(newMoney("101"), ca, TransactionPurpose.SEND);

    assertTrue(thirdPartyTransactionService.isTransactionAmountValid(transaction));
  }

}