package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.InterestTransactionServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class InterestTransactionServiceTest {

  @InjectMocks
  @Spy
  private InterestTransactionService interestTransactionService = new InterestTransactionServiceImpl();

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
  void testNewTransaction_savingsAccount_completeNewTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("1000"), ah);
    sa.setId(1L);
    sa.setInterestRate(new BigDecimal("0.01"));
    when(accountService.getById(1L)).thenReturn(sa);
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(interestTransactionService).validateTransaction(any());

    interestTransactionService.newTransaction(1L);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(argumentCaptor.capture());

    assertEquals(newMoney("10"), argumentCaptor.getValue().getBaseAmount());
    verify(interestTransactionService).validateTransaction(any());
    verify(accountService).updateBalance(any(Account.class));
  }

  @Test
  @Order(1)
  void testNewTransaction_creditCard_completeNewTransaction() {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var cc = new CreditCard(newMoney("1000"), ah);
    cc.setId(1L);
    cc.setInterestRate(new BigDecimal("0.12"));
    when(accountService.getById(1L)).thenReturn(cc);
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(interestTransactionService).validateTransaction(any());

    interestTransactionService.newTransaction(1L);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("10"), argumentCaptor.getValue().getBaseAmount());
    verify(interestTransactionService).validateTransaction(any());
    verify(accountService).updateBalance(any(Account.class));
  }

  @Test
  @Order(1)
  void testNewTransaction_CheckingAccount_throwException() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("1000"), ah);
    ca.setId(1L);
    when(accountService.getById(1L)).thenReturn(ca);

    assertThrows(IllegalArgumentException.class, () -> interestTransactionService.newTransaction(1L));
    verify(accountService).getById(1L);
  }


  // ============================== validate and process transaction Methods ==============================
  // ==================== Validate Transaction ====================
  @Test
  @Order(2)
  void testValidateTransaction_frozenAccount_refuseTransaction() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("100"), ah);
    sa.setId(1L);
    sa.setAccountStatus(AccountStatus.FROZEN);
    var transaction = new Transaction(newMoney("10"), sa);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(true);

    interestTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(sa, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.REFUSED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("0"), argumentCaptor.getValue().getBaseAmount());
    assertEquals("Account is frozen. Unable to add interest rate.", argumentCaptor.getValue().getDetails());
    verify(accountService).save(any(Account.class));
  }

  @Test
  @Order(2)
  void testValidateTransaction_allValid_acceptTransactionAndProcessIt() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("100"), ah);
    sa.setId(1L);
    var transaction = new Transaction(newMoney("10"), sa);
    when(transactionService.isAccountFrozen(transaction)).thenReturn(false);
    doNothing().when(interestTransactionService).processTransaction(any());

    interestTransactionService.validateTransaction(transaction);

    var argumentCaptor = ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(argumentCaptor.capture());
    assertEquals(sa, argumentCaptor.getValue().getPersonalAccount());
    assertEquals(Status.ACCEPTED, argumentCaptor.getValue().getStatus());
    assertEquals(newMoney("10"), argumentCaptor.getValue().getBaseAmount());
    verify(interestTransactionService).processTransaction(transaction);
    verify(accountService).save(any(Account.class));
  }


  // ==================== Process Transaction ====================
  @Test
  @Order(3)
  void testProcessTransaction_savingsAccount() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("100"), ah);
    sa.setId(1L);
    sa.setLastInterestUpdate(LocalDate.now());
    var transaction = new Transaction(newMoney("10"), sa);
    when(accountService.getById(1L)).thenReturn(sa);

    interestTransactionService.processTransaction(transaction);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountService).save(argumentCaptor.capture());
    var capturedSavingsAccount = (SavingsAccount) argumentCaptor.getValue();
    assertEquals(newMoney("110"), capturedSavingsAccount.getBalance());
    assertEquals(LocalDate.now().plusYears(1), capturedSavingsAccount.getLastInterestUpdate());
  }


  @Test
  @Order(3)
  void testProcessTransaction_creditCard() {
    var pa = new Address("test", "test", "test", "test");
    var ah = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var cc = new CreditCard(newMoney("100"), ah);
    cc.setId(1L);
    cc.setLastInterestUpdate(LocalDate.now());
    var transaction = new Transaction(newMoney("10"), cc);
    when(accountService.getById(1L)).thenReturn(cc);

    interestTransactionService.processTransaction(transaction);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountService).save(argumentCaptor.capture());
    var capturedSavingsAccount = (CreditCard) argumentCaptor.getValue();
    assertEquals(newMoney("110"), capturedSavingsAccount.getBalance());
    assertEquals(LocalDate.now().plusMonths(1), capturedSavingsAccount.getLastInterestUpdate());
  }


}