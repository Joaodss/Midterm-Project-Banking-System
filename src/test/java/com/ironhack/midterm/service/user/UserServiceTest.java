package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.repository.user.UserRepository;
import com.ironhack.midterm.service.user.Impl.UserServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService = new UserServiceImpl();

  @Mock
  private UserRepository userRepository;

  @Mock
  private AccountHolderRepository accountHolderRepository;


  // ======================================== get Methods ========================================
  // ==================== Get All ====================
  @Test
  @Order(1)
  void testGetAll_usesUserRepositoryFindAll() {
    userService.getAll();
    verify(userRepository).findAll();
    verifyNoMoreInteractions(userRepository);
  }


  // ==================== Get by Id ====================
  @Test
  @Order(2)
  void testGetById_usesUserRepositoryFindById() {
    try {
      userService.getById(any());
    } catch (EntityNotFoundException ignored) {
    }
    verify(userRepository).findById(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @Order(2)
  void testGetById_validId_returnsValidUser() {
    var admin = new Admin("admin", "admin", "Admin");
    admin.setId(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

    var storedUser = userService.getById(1L);
    assertEquals(admin, storedUser);
  }


  @Test
  @Order(2)
  void testGetById_invalidId_returnsException() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> userService.getById(99L));
  }


  // ==================== Get By Username ====================
  @Test
  @Order(2)
  void testGetByUsername_usesUserRepositoryFindById() {
    try {
      userService.getByUsername(any());
    } catch (EntityNotFoundException ignored) {
    }
    verify(userRepository).findByUsername(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @Order(2)
  void testGetByUsername_validId_returnsValidUser() {
    var admin = new Admin("admin", "admin", "Admin");
    admin.setId(1L);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

    var storedUser = userService.getByUsername("admin");
    assertEquals(admin, storedUser);
  }

  @Test
  @Order(2)
  void testGetByUsername_invalidId_returnsException() {
    when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> userService.getByUsername("noUser"));
  }


  // ======================================== edit Methods ========================================
  // ==================== Edit Password ====================
  @Test
  @Order(3)
  void testEditPassword_correctParameters_saves() {
    var admin = new Admin("admin", "admin", "Admin");
    admin.setId(1L);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

    userService.editPassword("admin", new UserEditPasswordDTO("admin", "password", "password"));
    verify(userRepository).findByUsername("admin");

    var argumentCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(argumentCaptor.capture());
  }

  @Test
  @Order(3)
  void testEditPassword_incorrectCurrentPassword_throwsException() {
    var admin = new Admin("admin", "admin", "Admin");
    admin.setId(1L);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

    assertThrows(IllegalArgumentException.class,
        () -> userService.editPassword("admin", new UserEditPasswordDTO("incorrectPassword", "password", "password")));
  }

  @Test
  @Order(3)
  void testEditPassword_incorrectNewPasswords_throwsException() {
    var admin = new Admin("admin", "admin", "Admin");
    admin.setId(1L);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

    assertThrows(IllegalArgumentException.class,
        () -> userService.editPassword("admin", new UserEditPasswordDTO("admin", "password", "different")));
  }


  // ==================== Edit ====================
  @Test
  @Order(4)
  void testEdit_correctParameters_UserAdmin_saves() {
    var user = new Admin("admin", "admin", "Admin");
    user.setId(1L);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

    var changes = new UserEditDTO();
    changes.setUsername("newAdmin");
    changes.setName("New Admin");

    userService.edit("admin", changes);
    verify(userRepository).findByUsername("admin");

    var argumentCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(argumentCaptor.capture());
    assertEquals("newAdmin", argumentCaptor.getValue().getUsername());
    assertEquals("New Admin", argumentCaptor.getValue().getName());
  }

  @Test
  @Order(4)
  void testEdit_correctParameters_AccountHolderWithPrimaryAddress_saves() {
    var pa = new Address("home", "1000", "city", "country");
    var user = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), pa);
    user.setId(1L);
    when(userRepository.findByUsername("joaodss")).thenReturn(Optional.of(user));

    var changes = new UserEditDTO();
    changes.setName("João");
    changes.setDateOfBirth(LocalDate.parse("2000-01-01"));
    changes.setPaCity("Coimbra");
    changes.setPaCountry("Portugal");
    changes.setMaCity("Blaaa");

    userService.edit("joaodss", changes);
    verify(userRepository).findByUsername("joaodss");

    var argumentCaptor = ArgumentCaptor.forClass(AccountHolder.class);
    verify(accountHolderRepository).save(argumentCaptor.capture());
    assertEquals("João", argumentCaptor.getValue().getName());
    assertEquals(LocalDate.parse("2000-01-01"), argumentCaptor.getValue().getDateOfBirth());
    assertEquals("Coimbra", argumentCaptor.getValue().getPrimaryAddress().getCity());
    assertEquals("Portugal", argumentCaptor.getValue().getPrimaryAddress().getCountry());
    assertNull(argumentCaptor.getValue().getMailingAddress());
  }

  @Test
  @Order(4)
  void testEdit_correctParameters_AccountHolderAllAddresses_saves() {
    var pa = new Address("home", "1000", "city", "country");
    var ma = new Address("mailHome", "2000", "Other City", "country");
    var user = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), pa, ma);
    user.setId(1L);
    when(userRepository.findByUsername("joaodss")).thenReturn(Optional.of(user));

    var changes = new UserEditDTO();
    changes.setName("João");
    changes.setDateOfBirth(LocalDate.parse("2000-01-01"));
    changes.setPaCity("Coimbra");
    changes.setPaCountry("Portugal");
    changes.setMaCity("Blaaa");
    changes.setMaCountry("Bleee");

    userService.edit("joaodss", changes);
    verify(userRepository).findByUsername("joaodss");

    var argumentCaptor = ArgumentCaptor.forClass(AccountHolder.class);
    verify(accountHolderRepository).save(argumentCaptor.capture());
    assertEquals("João", argumentCaptor.getValue().getName());
    assertEquals(LocalDate.parse("2000-01-01"), argumentCaptor.getValue().getDateOfBirth());
    assertEquals("Coimbra", argumentCaptor.getValue().getPrimaryAddress().getCity());
    assertEquals("Portugal", argumentCaptor.getValue().getPrimaryAddress().getCountry());
    assertEquals("Blaaa", argumentCaptor.getValue().getMailingAddress().getCity());
    assertEquals("Bleee", argumentCaptor.getValue().getMailingAddress().getCountry());
  }

  @Test
  @Order(4)
  void testEdit_correctParameters_AccountHolderNew_saves() {
    var pa = new Address("home", "1000", "city", "country");
    var user = new AccountHolder("joaodss", "12345", "João Afonso", LocalDate.parse("1996-10-01"), pa);
    user.setId(1L);
    when(userRepository.findByUsername("joaodss")).thenReturn(Optional.of(user));

    var changes = new UserEditDTO();
    changes.setUsername("newUsername1");
    changes.setMaStreetAddress("Bliii");
    changes.setMaPostalCode("Blooo");
    changes.setMaCity("Blaaa");
    changes.setMaCountry("Bleee");

    userService.edit("joaodss", changes);
    verify(userRepository).findByUsername("joaodss");

    var argumentCaptor = ArgumentCaptor.forClass(AccountHolder.class);
    verify(accountHolderRepository).save(argumentCaptor.capture());
    assertEquals("newUsername1", argumentCaptor.getValue().getUsername());
    assertEquals("Bliii", argumentCaptor.getValue().getMailingAddress().getStreetAddress());
    assertEquals("Blooo", argumentCaptor.getValue().getMailingAddress().getPostalCode());
    assertEquals("Blaaa", argumentCaptor.getValue().getMailingAddress().getCity());
    assertEquals("Bleee", argumentCaptor.getValue().getMailingAddress().getCountry());
  }


  // ======================================== util Methods ========================================
  // ==================== Is Username Present ====================
  @Test
  @Order(5)
  void testIsUsernamePresent_usesUserRepositoryFindByUsername() {
    try {
      userService.getByUsername(any());
    } catch (EntityNotFoundException ignored) {
    }
    verify(userRepository).findByUsername(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  @Order(5)
  void testIsUsernamePresent_validUsername_returnsTrue() {
    var admin = new Admin("admin", "admin", "Admin");
    admin.setId(1L);
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
    assertTrue(userService.isUsernamePresent("admin"));
  }

  @Test
  @Order(5)
  void testIsUsernamePresent_isInvalidUsername_returnsFalse() {
    when(userRepository.findByUsername("noUser")).thenReturn(Optional.empty());
    assertFalse(userService.isUsernamePresent("noUser"));
  }


}