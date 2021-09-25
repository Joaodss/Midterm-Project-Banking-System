package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.repository.account.StudentCheckingAccountRepository;
import com.ironhack.midterm.service.account.impl.StudentCheckingAccountServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.management.InstanceNotFoundException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class StudentCheckingAccountServiceTest {

  @InjectMocks
  private StudentCheckingAccountService studentCheckingAccountService = new StudentCheckingAccountServiceImpl();

  @Mock
  private StudentCheckingAccountRepository studentCheckingAccountRepository;


  // ======================================== get Methods ========================================
  @Test
  @Order(1)
  void testGetAll_usesStudentCheckingAccountRepositoryFindAllJoined() {
    studentCheckingAccountService.getAll();
    verify(studentCheckingAccountRepository).findAllJoined();
    verifyNoMoreInteractions(studentCheckingAccountRepository);
  }


  // ======================================== new Methods ========================================
  @Test
  @Order(2)
  void newAccount_savesStudentAccount() throws InstanceNotFoundException, NoSuchAlgorithmException {
    studentCheckingAccountService.newAccount(new StudentCheckingAccount());
    verify(studentCheckingAccountRepository).save(any(StudentCheckingAccount.class));
    verifyNoMoreInteractions(studentCheckingAccountRepository);
  }


}