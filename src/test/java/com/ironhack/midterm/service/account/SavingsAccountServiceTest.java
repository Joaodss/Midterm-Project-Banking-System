package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.account.SavingsAccountRepository;
import com.ironhack.midterm.service.account.impl.SavingsAccountServiceImpl;
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

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

  @InjectMocks
  private SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();

  @Mock
  private SavingsAccountRepository savingsAccountRepository;

  @Mock
  private AccountHolderService accountHolderService;


  // ======================================== get Methods ========================================
  @Test
  @Order(1)
  void testGetAll_usesCSavingsAccountRepositoryFindAllJoined() {
    savingsAccountService.getAll();
    verify(savingsAccountRepository).findAllJoined();
    verifyNoMoreInteractions(savingsAccountRepository);
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

    savingsAccountService.newAccount(accountDTO);

    verify(accountHolderService).findAccountHolders(accountDTO);
    var argumentCaptor = ArgumentCaptor.forClass(SavingsAccount.class);
    verify(savingsAccountRepository).save(argumentCaptor.capture());
    assertEquals(newMoney("10000"), argumentCaptor.getValue().getBalance());
    assertEquals(user1, argumentCaptor.getValue().getPrimaryOwner());
    assertNull(argumentCaptor.getValue().getSecondaryOwner());
  }

}