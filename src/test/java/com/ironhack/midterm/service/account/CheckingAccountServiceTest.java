package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.account.CheckingAccountRepository;
import com.ironhack.midterm.service.account.impl.CheckingAccountServiceImpl;
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
import org.springframework.test.context.ActiveProfiles;

import javax.management.InstanceNotFoundException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class CheckingAccountServiceTest {

  @InjectMocks
  private CheckingAccountService checkingAccountService = new CheckingAccountServiceImpl();

  @Mock
  private CheckingAccountRepository checkingAccountRepository;

  @Mock
  private AccountHolderService accountHolderService;

  @Mock
  private StudentCheckingAccountService studentCheckingAccountService;


  // ======================================== get Methods ========================================
  @Test
  @Order(1)
  void testGetAll_usesCheckingAccountRepositoryFindAllJoined_usesStudentCheckingAccountServiceGetAll() {
    checkingAccountService.getAll();
    verify(checkingAccountRepository).findAllJoined();
    verify(studentCheckingAccountService).getAll();
    verifyNoMoreInteractions(checkingAccountRepository);
    verifyNoMoreInteractions(studentCheckingAccountService);
  }

  @Test
  @Order(1)
  void testGetAll_getOnlyFromCheckingAccounts() throws NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var ca = new CheckingAccount(newMoney("1000"), new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1996-10-01"), pa));
    ca.setId(1L);
    var sca = new StudentCheckingAccount(newMoney("200"), new AccountHolder("afonso", "12345", "Afonso", LocalDate.parse("1996-10-01"), pa));
    sca.setId(2L);
    when(checkingAccountRepository.findAllJoined()).thenReturn(List.of(ca, sca));
    when(studentCheckingAccountService.getAll()).thenReturn(List.of(sca));

    var checkingAccounts = checkingAccountService.getAll();

    assertTrue(checkingAccounts.contains(ca));
    assertFalse(checkingAccounts.contains(sca));
  }


  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void newAccount_youngPerson_newStudentAccoun() throws InstanceNotFoundException, NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user1 = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("2010-10-01"), pa);
    user1.setId(1L);
    var list = new AccountHolder[2];
    list[0] = user1;
    list[1] = null;

    var accountDTO = new AccountDTO();
    accountDTO.setInitialBalance(new BigDecimal("1000"));
    accountDTO.setCurrency("EUR");
    accountDTO.setPrimaryOwnerId(1L);
    accountDTO.setPrimaryOwnerUsername("joaodss");
    when(accountHolderService.findAccountHolders(accountDTO)).thenReturn(list);

    checkingAccountService.newAccount(accountDTO);

    verify(accountHolderService).findAccountHolders(accountDTO);
    var argumentCaptor = ArgumentCaptor.forClass(StudentCheckingAccount.class);
    verify(studentCheckingAccountService).newAccount(argumentCaptor.capture());
    assertEquals(newMoney("1000"), argumentCaptor.getValue().getBalance());
    assertEquals(user1, argumentCaptor.getValue().getPrimaryOwner());
    assertNull(argumentCaptor.getValue().getSecondaryOwner());
  }

  @Test
  @Order(2)
  void newAccount_oldPerson_saveCheckingAccount() throws InstanceNotFoundException, NoSuchAlgorithmException {
    var pa = new Address("test", "test", "test", "test");
    var user1 = new AccountHolder("joaodss", "12345", "João", LocalDate.parse("1910-10-01"), pa);
    user1.setId(1L);
    var list = new AccountHolder[2];
    list[0] = user1;
    list[1] = null;

    var accountDTO = new AccountDTO();
    accountDTO.setInitialBalance(new BigDecimal("1000"));
    accountDTO.setCurrency("EUR");
    accountDTO.setPrimaryOwnerId(1L);
    accountDTO.setPrimaryOwnerUsername("joaodss");
    when(accountHolderService.findAccountHolders(accountDTO)).thenReturn(list);

    checkingAccountService.newAccount(accountDTO);

    verify(accountHolderService).findAccountHolders(accountDTO);
    var argumentCaptor = ArgumentCaptor.forClass(CheckingAccount.class);
    verify(checkingAccountRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("1000"), argumentCaptor.getValue().getBalance());
    assertEquals(user1, argumentCaptor.getValue().getPrimaryOwner());
    assertNull(argumentCaptor.getValue().getSecondaryOwner());
  }


}