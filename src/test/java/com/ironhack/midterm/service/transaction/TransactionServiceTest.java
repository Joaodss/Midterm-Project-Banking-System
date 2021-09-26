package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.repository.transaction.TransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.TransactionServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @InjectMocks
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



}