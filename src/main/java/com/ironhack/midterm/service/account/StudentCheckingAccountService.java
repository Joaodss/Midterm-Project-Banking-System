package com.ironhack.midterm.service.account;

import com.ironhack.midterm.dao.account.StudentCheckingAccount;

import java.util.List;

public interface StudentCheckingAccountService {

  // ======================================== get Methods ========================================
  List<StudentCheckingAccount> getAll();

  // ======================================== new Methods ========================================
  void newAccount(StudentCheckingAccount studentCheckingAccount);

  // ======================================== update balance Methods ========================================

}
