package com.ironhack.midterm.service.user;

import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.repository.user.RoleRepository;
import com.ironhack.midterm.service.user.Impl.RoleServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

  @InjectMocks
  private RoleService roleService = new RoleServiceImpl();

  @Mock
  private RoleRepository roleRepository;


  // ======================================== get Methods ========================================
  // ==================== Get All ====================
  @Test
  @Order(1)
  void testGetByName_usesRoleRepositoryFindByName() {
    roleService.getByName(any());
    verify(roleRepository).findByName(any());
    verifyNoMoreInteractions(roleRepository);
  }

  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void testSave_usesRoleRepositorySave() {
    roleService.newRole(any(String.class));
    verify(roleRepository).save(any(Role.class));
    verifyNoMoreInteractions(roleRepository);
  }

  @Test
  @Order(2)
  void testSave_usesRoleRepositorySave_roleSavedHasSameName() {
    var argumentCaptor = ArgumentCaptor.forClass(Role.class);
    roleService.newRole("ROLE");
    verify(roleRepository).save(argumentCaptor.capture());
    assertEquals("ROLE", argumentCaptor.getValue().getName());
  }

}