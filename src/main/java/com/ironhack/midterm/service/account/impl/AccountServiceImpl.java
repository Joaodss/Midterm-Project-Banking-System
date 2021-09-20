package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.repository.account.AccountRepository;
import com.ironhack.midterm.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  AccountRepository accountRepository;


  // ======================================== GET Methods ========================================
  public List<Account> getAll() {
    return accountRepository.findAllJoined();
  }

  public Account getById(Long id) throws InstanceNotFoundException {
    var account = accountRepository.findByIdJoined(id);
    if (account.isPresent()) return account.get();
    throw new InstanceNotFoundException();
  }


}
