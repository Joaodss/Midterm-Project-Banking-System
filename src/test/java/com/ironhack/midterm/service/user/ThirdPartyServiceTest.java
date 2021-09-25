package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.repository.user.ThirdPartyRepository;
import com.ironhack.midterm.service.user.Impl.ThirdPartyServiceImpl;
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

import javax.persistence.EntityExistsException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ThirdPartyServiceTest {

  @InjectMocks
  private ThirdPartyService thirdPartyService = new ThirdPartyServiceImpl();

  @Mock
  private ThirdPartyRepository thirdPartyRepository;

  @Mock
  private UserService userService;

  @Mock
  private RoleService roleService;


  // ======================================== get Methods ========================================
  // ==================== Get All ====================
  @Test
  @Order(1)
  void testGetAll_usesThirdPartyRepositoryFindAll() {
    thirdPartyService.getAll();
    verify(thirdPartyRepository).findAll();
    verifyNoMoreInteractions(thirdPartyRepository);
  }

  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void testNewUser_newUsername_newRole_usesThirdPartyRepositorySave() throws EntityExistsException {
    var newThirdParty = new UserDTO("tp", "12345", "Third Party");
    var role = new Role("THIRD_PARTY");
    role.setId(1);
    when(userService.isUsernamePresent("tp")).thenReturn(false);
    when(roleService.getByName("THIRD_PARTY")).thenReturn(Optional.of(role));

    thirdPartyService.newUser(newThirdParty);

    var argumentCaptor = ArgumentCaptor.forClass(ThirdParty.class);
    verify(thirdPartyRepository).save(argumentCaptor.capture());
    verifyNoMoreInteractions(thirdPartyRepository);
    assertEquals("Third Party", argumentCaptor.getValue().getName());
    assertEquals("THIRD_PARTY", argumentCaptor.getValue().getRoles().stream().findFirst().get().getName());
  }

  @Test
  @Order(2)
  void testNewUser_newUsername_existingRole_usesThirdPartyRepositorySave() throws EntityExistsException {
    var newThirdParty = new UserDTO("tp", "12345", "Third Party");
    var role = new Role("THIRD_PARTY");
    role.setId(1);
    when(userService.isUsernamePresent("tp")).thenReturn(false);
    when(roleService.getByName("THIRD_PARTY")).thenReturn(Optional.empty()).thenReturn(Optional.of(role));

    thirdPartyService.newUser(newThirdParty);

    var argumentCaptor = ArgumentCaptor.forClass(ThirdParty.class);
    verify(roleService).newRole("THIRD_PARTY");
    verify(thirdPartyRepository).save(argumentCaptor.capture());
    verifyNoMoreInteractions(thirdPartyRepository);
    assertEquals("Third Party", argumentCaptor.getValue().getName());
    assertEquals("THIRD_PARTY", argumentCaptor.getValue().getRoles().stream().findFirst().get().getName());
  }

  @Test
  @Order(2)
  void testNewUser_existingUsername_throwsException() {
    var newThirdParty = new UserDTO("tp", "12345", "Third Party");
    when(userService.isUsernamePresent("tp")).thenReturn(true);

    assertThrows(EntityExistsException.class, () -> thirdPartyService.newUser(newThirdParty));
  }


  // ======================================== utils Methods ========================================
  @Test
  @Order(3)
  void testHasHashedKey_validHashedKey_true() {
    when(thirdPartyRepository.findByHashedKey(any())).thenReturn(Optional.of(new ThirdParty()));

    assertTrue(thirdPartyService.hasHashedKey(any()));
    verify(thirdPartyRepository).findByHashedKey(any());
    verifyNoMoreInteractions(thirdPartyRepository);
  }

  @Test
  @Order(3)
  void testHasHashedKey_invalidHashedKey_false() {
    when(thirdPartyRepository.findByHashedKey(any())).thenReturn(Optional.empty());

    assertFalse(thirdPartyService.hasHashedKey(any()));
    verify(thirdPartyRepository).findByHashedKey(any());
    verifyNoMoreInteractions(thirdPartyRepository);
  }


}