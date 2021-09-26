package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CreditCard;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.account.CreditCardRepository;
import com.ironhack.midterm.service.account.impl.CreditCardServiceImpl;
import com.ironhack.midterm.service.transaction.InterestTransactionService;
import com.ironhack.midterm.service.transaction.PenaltyFeeTransactionService;
import com.ironhack.midterm.service.user.AccountHolderService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {

  @InjectMocks
  private CreditCardService creditCardService = new CreditCardServiceImpl();

  @Mock
  private CreditCardRepository creditCardRepository;

  @Mock
  private AccountHolderService accountHolderService;

  @Mock
  private InterestTransactionService interestTransactionService;

  @Mock
  private PenaltyFeeTransactionService penaltyFeeTransactionService;


  // ======================================== get Methods ========================================
  @Test
  @Order(1)
  void testGetAll_usesCSavingsAccountRepositoryFindAllJoined() {
    creditCardService.getAll();
    verify(creditCardRepository).findAllJoined();
    verifyNoMoreInteractions(creditCardRepository);
  }


  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void newAccount_saveSavingsAccount() throws InstanceNotFoundException, NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user1 = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("2010-10-01"), pa);
    user1.setId(1L);
    var list = new AccountHolder[2];
    list[0] = user1;
    list[1] = null;

    var accountDTO = new AccountDTO();
    accountDTO.setInitialBalance(new BigDecimal("10000"));
    accountDTO.setCurrency("EUR");
    accountDTO.setPrimaryOwnerId(1L);
    accountDTO.setPrimaryOwnerUsername("joaodss");
    when(accountHolderService.findAccountHolders(accountDTO)).thenReturn(list);

    creditCardService.newAccount(accountDTO);

    verify(accountHolderService).findAccountHolders(accountDTO);
    var argumentCaptor = ArgumentCaptor.forClass(CreditCard.class);
    verify(creditCardRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("10000"), argumentCaptor.getValue().getBalance());
    assertEquals(user1, argumentCaptor.getValue().getPrimaryOwner());
    assertNull(argumentCaptor.getValue().getSecondaryOwner());
  }


  // ======================================== update balance Methods ========================================
  @Test
  @Order(3)
  void testCheckInterestUpdate_expiredInterestDate_CallMethodsToUpdate() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var cc = new CreditCard(newMoney("700"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    cc.setId(1L);
    cc.setLastInterestUpdate(LocalDate.parse("2021-01-01"));

    creditCardService.checkInterestRate(cc);

    verify(interestTransactionService).newTransaction(1L);
    verifyNoMoreInteractions(interestTransactionService);
  }

  @Test
  @Order(3)
  void testCheckInterestUpdate_currentInterestDate_DoNothing() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var cc = new CreditCard(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    cc.setId(1L);

    creditCardService.checkInterestRate(cc);
    verifyNoInteractions(interestTransactionService);
  }


  @Test
  @Order(4)
  void testCheckMinimumBalance_expiredPenaltyFeeDate_LowBalance_processFee() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var cc = new CreditCard(newMoney("60"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    cc.setId(1L);
    cc.setLastPenaltyFeeCheck(LocalDate.parse("2021-01-01"));

    creditCardService.checkCreditLimit(cc);

    verify(penaltyFeeTransactionService).newTransaction(1L);
    verifyNoMoreInteractions(penaltyFeeTransactionService);
  }

  @Test
  @Order(4)
  void testCheckMinimumBalance_expiredPenaltyFeeDate_highBalance_updateLastDate() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var cc = new CreditCard(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    cc.setId(1L);
    cc.setLastPenaltyFeeCheck(LocalDate.parse("2021-01-01"));

    creditCardService.checkCreditLimit(cc);

    var argumentCaptor = ArgumentCaptor.forClass(CreditCard.class);
    verify(creditCardRepository).save(argumentCaptor.capture());
    verifyNoInteractions(penaltyFeeTransactionService);
    assertEquals(LocalDate.now().minusMonths(1).minusDays(1), argumentCaptor.getValue().getLastPenaltyFeeCheck());
  }

  @Test
  @Order(4)
  void testCheckMinimumBalance_recentPenaltyFeeDate_lowBalance_doNothing() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var cc = new CreditCard(newMoney("60"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    cc.setId(1L);
    cc.setLastPenaltyFeeCheck(LocalDate.now().minusDays(5));

    creditCardService.checkCreditLimit(cc);

    verifyNoInteractions(creditCardRepository);
    verifyNoInteractions(penaltyFeeTransactionService);
  }

}