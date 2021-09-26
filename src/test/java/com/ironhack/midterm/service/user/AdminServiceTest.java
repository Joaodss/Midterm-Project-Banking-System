package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.repository.user.AdminRepository;
import com.ironhack.midterm.service.user.Impl.AdminServiceImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  @InjectMocks
  private AdminService adminService = new AdminServiceImpl();

  @Mock
  private AdminRepository adminRepository;

  @Mock
  private UserService userService;

  @Mock
  private RoleService roleService;


  // ======================================== get Methods ========================================
  // ==================== Get All ====================
  @Test
  @Order(1)
  void testGetAll_usesAdminRepositoryFindAll() {
    adminService.getAll();
    verify(adminRepository).findAll();
    verifyNoMoreInteractions(adminRepository);
  }

  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void testNewUser_newUsername_newRole_usesAdminRepositorySave() {
    var newAdmin = new UserDTO("admin", "admin", "Admin");
    var role = new Role("ADMIN");
    role.setId(1);
    when(userService.isUsernamePresent("admin")).thenReturn(false);
    when(roleService.getByName("ADMIN")).thenReturn(Optional.of(role));

    adminService.newUser(newAdmin);

    var argumentCaptor = ArgumentCaptor.forClass(Admin.class);
    verify(adminRepository).save(argumentCaptor.capture());
    verifyNoMoreInteractions(adminRepository);
    assertEquals("Admin", argumentCaptor.getValue().getName());
    assertEquals("ADMIN", argumentCaptor.getValue().getRoles().stream().findFirst().get().getName());
  }

  @Test
  @Order(2)
  void testNewUser_newUsername_existingRole_usesAdminRepositorySave() {
    var newAdmin = new UserDTO("admin", "admin", "Admin");
    var role = new Role("ADMIN");
    role.setId(1);
    when(userService.isUsernamePresent("admin")).thenReturn(false);
    when(roleService.getByName("ADMIN")).thenReturn(Optional.empty()).thenReturn(Optional.of(role));

    adminService.newUser(newAdmin);

    var argumentCaptor = ArgumentCaptor.forClass(Admin.class);
    verify(roleService).newRole("ADMIN");
    verify(adminRepository).save(argumentCaptor.capture());
    verifyNoMoreInteractions(adminRepository);
    assertEquals("Admin", argumentCaptor.getValue().getName());
    assertEquals("ADMIN", argumentCaptor.getValue().getRoles().stream().findFirst().get().getName());
  }

  @Test
  @Order(2)
  void testNewUser_existingUsername_throwsException() {
    var newAdmin = new UserDTO("admin", "admin", "Admin");
    when(userService.isUsernamePresent("admin")).thenReturn(true);

    assertThrows(EntityExistsException.class, () -> adminService.newUser(newAdmin));
  }


}