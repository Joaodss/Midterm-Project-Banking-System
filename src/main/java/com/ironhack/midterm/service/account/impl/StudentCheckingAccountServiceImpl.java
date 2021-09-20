package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.repository.account.StudentCheckingAccountRepository;
import com.ironhack.midterm.service.account.StudentCheckingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCheckingAccountServiceImpl implements StudentCheckingAccountService {

  @Autowired
  private StudentCheckingAccountRepository studentCheckingAccountRepository;


  // ======================================== GET ACCOUNT Methods ========================================
  public List<StudentCheckingAccount> getAll() {
    return studentCheckingAccountRepository.findAllJoined();
  }


}
