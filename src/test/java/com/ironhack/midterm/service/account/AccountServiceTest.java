package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.*;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountEditDTO;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.account.AccountRepository;
import com.ironhack.midterm.service.account.impl.AccountServiceImpl;
import com.ironhack.midterm.service.user.UserService;
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
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @InjectMocks
  @Spy
  private AccountService accountService = new AccountServiceImpl();

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserService userService;


  // ======================================== get Methods ========================================
  // ==================== Get All ====================
  @Test
  @Order(1)
  void testGetAll_usesAccountRepositoryFindAllJoined() {
    accountService.getAll();
    verify(accountRepository).findAllJoined();
    verifyNoMoreInteractions(accountRepository);
  }


  // ==================== Get All By Username ====================
  @Test
  @Order(2)
  void testGetAllByUsername_usesAccountRepositoryFindAllByUsernameJoined_usesUpdateBalance() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new CheckingAccount(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    when(accountRepository.findAllByUsernameJoined("joaodss")).thenReturn(List.of(ca));

    accountService.getAllByUsername("joaodss");
    verify(accountRepository).findAllByUsernameJoined("joaodss");
    verifyNoMoreInteractions(accountRepository);
    verify(accountService).updateBalance(ca);
  }

  // ==================== Get By Id ====================
  @Test
  @Order(3)
  void testGetById_usesAccountRepositoryFindByIdJoined_usesUpdateBalance() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new CheckingAccount(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    ca.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(ca));

    accountService.getById(1L);
    verify(accountRepository).findByIdJoined(1L);
    verifyNoMoreInteractions(accountRepository);
    verify(accountService).updateBalance(ca);
  }

  @Test
  @Order(3)
  void testGetById_invalidId_throwsException() {
    when(accountRepository.findByIdJoined(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> accountService.getById(99L));
  }

  // ======================================== edit Methods ========================================
  @Test
  @Order(4)
  void edit_EditCheckingAccount_someValues_valuesAreEdited() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var ca = new CheckingAccount(newMoney("1000"), user);
    ca.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(ca));
    when(userService.getByUsername("joaodss")).thenReturn(user);

    var accountEdit = new AccountEditDTO();
    accountEdit.setPrimaryOwnerUsername("joaodss");
    accountEdit.setSecondaryOwnerUsername("joaodss");
    accountEdit.setAccountStatus("FROZEN");
    accountEdit.setCurrency("EUR");
    accountEdit.setMinimumBalance(new BigDecimal("200"));
    accountEdit.setMonthlyMaintenanceFee(new BigDecimal("20"));
    accountEdit.setLastMaintenanceFee(LocalDate.now());
    accountEdit.setAccountBalance(new BigDecimal("290"));
    accountEdit.setPenaltyFee(new BigDecimal("10"));
    accountService.edit(1L, accountEdit);

    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).findByIdJoined(1L);
    verify(userService, times(2)).getByUsername("joaodss");
    verify(accountRepository).save(argumentCaptor.capture());
    var capture = (CheckingAccount) argumentCaptor.getValue();
    assertEquals("joaodss", capture.getPrimaryOwner().getUsername());
    assertEquals("joaodss", capture.getSecondaryOwner().getUsername());
    assertEquals(Currency.getInstance("EUR"), capture.getBalance().getCurrency());
    assertEquals(new BigDecimal("200.00"), capture.getMinimumBalance().getAmount());
    assertEquals(new BigDecimal("20.00"), capture.getMonthlyMaintenanceFee().getAmount());
    assertEquals(new BigDecimal("290.00"), capture.getBalance().getAmount());
    assertEquals(new BigDecimal("10.00"), capture.getPenaltyFee().getAmount());
  }

  @Test
  @Order(4)
  void edit_EditStudentCheckingAccount_OneValue_valueEdited() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sca = new StudentCheckingAccount(newMoney("1000"), user);
    sca.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(sca));

    var accountEdit = new AccountEditDTO();
    accountEdit.setLastPenaltyFee(LocalDate.parse("2022-01-01"));
    accountService.edit(1L, accountEdit);

    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).findByIdJoined(1L);
    verify(accountRepository).save(argumentCaptor.capture());
    var capture = (StudentCheckingAccount) argumentCaptor.getValue();
    assertEquals(LocalDate.parse("2022-01-01"), capture.getLastPenaltyFeeCheck());
  }

  @Test
  @Order(4)
  void edit_EditSavingsAccount_SomeValue_valueEdited() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var sa = new SavingsAccount(newMoney("1000"), user);
    sa.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(sa));

    var accountEdit = new AccountEditDTO();
    accountEdit.setLastPenaltyFee(LocalDate.parse("2022-01-01"));
    accountEdit.setSavingsAccountInterestRate(new BigDecimal("0.01"));
    accountEdit.setLastInterestUpdate(LocalDate.parse("2022-01-01"));
    accountService.edit(1L, accountEdit);

    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).findByIdJoined(1L);
    verify(accountRepository).save(argumentCaptor.capture());
    var capture = (SavingsAccount) argumentCaptor.getValue();
    assertEquals(LocalDate.parse("2022-01-01"), capture.getLastPenaltyFeeCheck());
    assertEquals(LocalDate.parse("2022-01-01"), capture.getLastInterestUpdate());
    assertEquals(new BigDecimal("0.01"), capture.getInterestRate());
  }

  @Test
  @Order(4)
  void edit_EditCreditCard_SomeValue_valueEdited() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa);
    var cc = new CreditCard(newMoney("1000"), user);
    cc.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(cc));

    var accountEdit = new AccountEditDTO();
    accountEdit.setLastPenaltyFee(LocalDate.parse("2022-01-01"));
    accountEdit.setSavingsAccountInterestRate(new BigDecimal("0.01"));
    accountEdit.setLastInterestUpdate(LocalDate.parse("2022-01-01"));
    accountEdit.setAccountStatus("FROZEN"); //does nothing
    accountService.edit(1L, accountEdit);

    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).findByIdJoined(1L);
    verify(accountRepository).save(argumentCaptor.capture());
    var capture = (CreditCard) argumentCaptor.getValue();
    assertEquals(LocalDate.parse("2022-01-01"), capture.getLastPenaltyFeeCheck());
    assertEquals(LocalDate.parse("2022-01-01"), capture.getLastInterestUpdate());
    assertNotEquals(new BigDecimal("0.01"), capture.getInterestRate()); //use wrong interest rate
  }


  // ======================================== utils Methods ========================================
  // ==================== Save ====================
  @Test
  @Order(5)
  void testSave_usesAccountRepositorySave() {
    accountService.save(new CheckingAccount());
    verify(accountRepository).save(any(Account.class));
    verifyNoMoreInteractions(accountRepository);
  }

  // ==================== Freeze Account ====================
  @Test
  @Order(6)
  void freezeAccount_checkingAccount_freeze() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new CheckingAccount(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    ca.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(ca));

    accountService.freezeAccount(1L);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(argumentCaptor.capture());
    CheckingAccount savedAccount = (CheckingAccount) argumentCaptor.getValue();
    assertEquals(CheckingAccount.class, savedAccount.getClass());
    assertEquals(AccountStatus.FROZEN, savedAccount.getAccountStatus());
  }

  @Test
  @Order(6)
  void freezeAccount_studentCheckingAccount_freeze() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new StudentCheckingAccount(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    ca.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(ca));

    accountService.freezeAccount(1L);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(argumentCaptor.capture());
    StudentCheckingAccount savedAccount = (StudentCheckingAccount) argumentCaptor.getValue();
    assertEquals(StudentCheckingAccount.class, savedAccount.getClass());
    assertEquals(AccountStatus.FROZEN, savedAccount.getAccountStatus());
  }

  @Test
  @Order(6)
  void freezeAccount_savingsAccount_freeze() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new SavingsAccount(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    ca.setId(1L);
    when(accountRepository.findByIdJoined(1L)).thenReturn(Optional.of(ca));

    accountService.freezeAccount(1L);

    verify(accountService).getById(1L);
    var argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(argumentCaptor.capture());
    SavingsAccount savedAccount = (SavingsAccount) argumentCaptor.getValue();
    assertEquals(SavingsAccount.class, savedAccount.getClass());
    assertEquals(AccountStatus.FROZEN, savedAccount.getAccountStatus());
  }

  @Test
  @Order(6)
  void freezeAccount_creditCard_doNothing() {
    verifyNoInteractions(accountRepository);
  }

  // ==================== Update balance ====================
  void updateBalance() {

  }

}