package com.ironhack.midterm.service.account.impl;

import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.repository.account.SavingsAccountRepository;
import com.ironhack.midterm.service.account.SavingsAccountService;
import com.ironhack.midterm.service.user.AccountHolderService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Service
public class SavingsAccountServiceImpl implements SavingsAccountService {

  @Autowired
  private SavingsAccountRepository savingsAccountRepository;


  @Autowired
  private AccountHolderService accountHolderService;


  // ======================================== GET ACCOUNT Methods ========================================
  public List<SavingsAccount> getAll() {
    return savingsAccountRepository.findAllJoined();
  }

  // ======================================== ADD ACCOUNT Methods ========================================
  public void newAccount(AccountDTO savingsAccount) throws InstanceNotFoundException, IllegalArgumentException, NoSuchAlgorithmException {
    // Perform an identity check of both account owners
    AccountHolder[] accountHolders = accountHolderService.findAccountHolders(savingsAccount);

    SavingsAccount sa = new SavingsAccount(newMoney(savingsAccount.getInitialBalance().toString(), savingsAccount.getCurrency()), accountHolders[0], accountHolders[1]);
    sa.updateCurrencyValues();

    savingsAccountRepository.save(sa);
  }


}
