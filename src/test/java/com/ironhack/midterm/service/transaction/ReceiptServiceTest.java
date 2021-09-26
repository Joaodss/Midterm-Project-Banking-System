package com.ironhack.midterm.service.transaction;

import com.ironhack.midterm.dao.transaction.Receipt;
import com.ironhack.midterm.repository.transaction.ReceiptRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.impl.ReceiptServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

  @InjectMocks
  private ReceiptService receiptService = new ReceiptServiceImpl();

  @Mock
  private ReceiptRepository receiptRepository;

  @Mock
  private AccountService accountService;


  // ======================================== get Methods ========================================
  // ==================== Get Receipt By Transaction Id ====================
  @Test
  @Order(1)
  void testGetReceiptByTransactionId_validIds_returnReceipt() {
    when(accountService.hasAccount(1L)).thenReturn(true);
    when(receiptRepository.findByTransactionIdJoined(1L, 1L)).thenReturn(Optional.of(new Receipt()));

    assertEquals(Receipt.class, receiptService.getReceiptByTransactionId(1L, 1L).getClass());

    verify(accountService).hasAccount(1L);
    verify(receiptRepository).findByTransactionIdJoined(1L, 1L);
    verifyNoMoreInteractions(accountService);
    verifyNoMoreInteractions(receiptRepository);
  }

  @Test
  @Order(1)
  void testGetReceiptByTransactionId_invalidAccountId_throwException() {
    when(accountService.hasAccount(99L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class, () -> receiptService.getReceiptByTransactionId(99L, 1L));

    verify(accountService).hasAccount(99L);
    verifyNoInteractions(receiptRepository);
  }

  @Test
  @Order(1)
  void testGetReceiptByTransactionId_idsNotMatch_throwException() {
    when(accountService.hasAccount(1L)).thenReturn(true);
    when(receiptRepository.findByTransactionIdJoined(1L, 1L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> receiptService.getReceiptByTransactionId(1L, 1L));

    verify(accountService).hasAccount(1L);
    verify(receiptRepository).findByTransactionIdJoined(1L, 1L);
    verifyNoMoreInteractions(accountService);
    verifyNoMoreInteractions(receiptRepository);
  }

}