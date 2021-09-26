package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.dto.UserAccountHolderDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.service.user.Impl.AccountHolderServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AccountHolderServiceTest {

  @InjectMocks
  private AccountHolderService accountHolderService = new AccountHolderServiceImpl();

  @Mock
  private AccountHolderRepository accountHolderRepository;

  @Mock
  private UserService userService;

  @Mock
  private RoleService roleService;


  // ======================================== get Methods ========================================
  // ==================== Get All ====================
  @Test
  @Order(1)
  void testGetAll_usesAccountHolderRepositoryFindAll() {
    accountHolderService.getAll();
    verify(accountHolderRepository).findAll();
    verifyNoMoreInteractions(accountHolderRepository);
  }

  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void testNewUser_newUsername_newRole_usesAccountHolderRepositorySave() throws EntityExistsException {
    var newAccountHolder = new UserAccountHolderDTO(
        "joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"),
        "Rua 1", "3000", "Coimbra", "Portugal",
        "Rua 2", "6000", "Porto", "Portugal"
    );
    var role = new Role("USER");
    role.setId(1);
    when(userService.isUsernamePresent("joaodss")).thenReturn(false);
    when(roleService.getByName("USER")).thenReturn(Optional.of(role));

    accountHolderService.newUser(newAccountHolder);

    var argumentCaptor = ArgumentCaptor.forClass(AccountHolder.class);
    verify(accountHolderRepository).save(argumentCaptor.capture());
    verifyNoMoreInteractions(accountHolderRepository);
    assertEquals("João Afonso", argumentCaptor.getValue().getName());
    assertEquals("USER", argumentCaptor.getValue().getRoles().stream().findFirst().get().getName());
  }

  @Test
  @Order(2)
  void testNewUser_newUsername_existingRole_usesAccountHolderRepositorySave() throws EntityExistsException {
    var newAccountHolder = new UserAccountHolderDTO();
    newAccountHolder.setUsername("joaodss");
    newAccountHolder.setPassword("12345");
    newAccountHolder.setName("João Afonso");
    newAccountHolder.setDateOfBirth(LocalDate.parse("1996-10-01"));
    newAccountHolder.setPaStreetAddress("Rua 1");
    newAccountHolder.setPaPostalCode("3000");
    newAccountHolder.setPaCity("Coimbra");
    newAccountHolder.setPaCountry("Portugal");
    var role = new Role("USER");
    role.setId(1);
    when(userService.isUsernamePresent("joaodss")).thenReturn(false);
    when(roleService.getByName("USER")).thenReturn(Optional.empty()).thenReturn(Optional.of(role));

    accountHolderService.newUser(newAccountHolder);

    var argumentCaptor = ArgumentCaptor.forClass(AccountHolder.class);
    verify(roleService).newRole("USER");
    verify(accountHolderRepository).save(argumentCaptor.capture());
    verifyNoMoreInteractions(accountHolderRepository);
    assertEquals("João Afonso", argumentCaptor.getValue().getName());
    assertEquals("USER", argumentCaptor.getValue().getRoles().stream().findFirst().get().getName());
  }

  @Test
  @Order(2)
  void testNewUser_existingUsername_throwsException() {
    var newAccountHolder = new UserAccountHolderDTO(
        "joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"),
        "Rua 1", "3000", "Coimbra", "Portugal",
        "Rua 2", "6000", "Porto", "Portugal"
    );
    when(userService.isUsernamePresent("joaodss")).thenReturn(true);

    assertThrows(EntityExistsException.class, () -> accountHolderService.newUser(newAccountHolder));
  }


  // ======================================== utils Methods ========================================
  @Test
  @Order(3)
  void testFindAccountHolders_BothUsersExist_returnBoth() {
    var address1 = new Address("Rua 1", "3000", "Coimbra", "Portugal");
    var address2 = new Address("Rua 2", "6000", "Porto", "Portugal");
    var newAccountHolder1 = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), address1, address2);
    newAccountHolder1.setId(1L);
    var newAccountHolder2 = new AccountHolder("test2", "12345", "Test", LocalDate.parse("1996-10-01"), address2);
    newAccountHolder2.setId(2L);

    var accountDTO = new AccountDTO(new BigDecimal("10"), "EUR", 1L, "joaodss", 2L, "test2");
    when(accountHolderRepository.findByUsername("joaodss")).thenReturn(Optional.of(newAccountHolder1));
    when(accountHolderRepository.findByUsername("test2")).thenReturn(Optional.of(newAccountHolder2));

    var response = accountHolderService.findAccountHolders(accountDTO);

    assertEquals(newAccountHolder1, response[0]);
    assertEquals(newAccountHolder2, response[1]);
    verify(accountHolderRepository).findByUsername("joaodss");
    verify(accountHolderRepository).findByUsername("test2");
    verifyNoMoreInteractions(accountHolderRepository);
  }

  @Test
  @Order(3)
  void testFindAccountHolders_OneUsersExist_returnUser() {
    var address1 = new Address("Rua 1", "3000", "Coimbra", "Portugal");
    var address2 = new Address("Rua 2", "6000", "Porto", "Portugal");
    var newAccountHolder1 = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), address1, address2);
    newAccountHolder1.setId(1L);

    var accountDTO = new AccountDTO();
    accountDTO.setInitialBalance(new BigDecimal("10"));
    accountDTO.setCurrency("EUR");
    accountDTO.setPrimaryOwnerId(1L);
    accountDTO.setPrimaryOwnerUsername("joaodss");
    when(accountHolderRepository.findByUsername("joaodss")).thenReturn(Optional.of(newAccountHolder1));

    var response = accountHolderService.findAccountHolders(accountDTO);

    assertEquals(newAccountHolder1, response[0]);
    assertNull(response[1]);
    verify(accountHolderRepository).findByUsername("joaodss");
    verifyNoMoreInteractions(accountHolderRepository);
  }

  @Test
  @Order(3)
  void testFindAccountHolders_OneUserDoesNotExist_throwException() {
    var address1 = new Address("Rua 1", "3000", "Coimbra", "Portugal");
    var address2 = new Address("Rua 2", "6000", "Porto", "Portugal");
    var newAccountHolder1 = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), address1, address2);
    newAccountHolder1.setId(1L);

    var accountDTO = new AccountDTO(new BigDecimal("10"), "EUR", 1L, "joaodss", 2L, "test2");
    when(accountHolderRepository.findByUsername("joaodss")).thenReturn(Optional.of(newAccountHolder1));
    when(accountHolderRepository.findByUsername("test2")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> accountHolderService.findAccountHolders(accountDTO));
  }

  @Test
  @Order(3)
  void testFindAccountHolders_OneUsernameDoesNotMatchId_throwException() {
    var address1 = new Address("Rua 1", "3000", "Coimbra", "Portugal");
    var address2 = new Address("Rua 2", "6000", "Porto", "Portugal");
    var newAccountHolder1 = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), address1, address2);
    newAccountHolder1.setId(1L);

    var accountDTO = new AccountDTO(new BigDecimal("10"), "EUR", 99L, "joaodss", 2L, "test2");
    when(accountHolderRepository.findByUsername("joaodss")).thenReturn(Optional.of(newAccountHolder1));

    assertThrows(IllegalArgumentException.class, () -> accountHolderService.findAccountHolders(accountDTO));
  }


}