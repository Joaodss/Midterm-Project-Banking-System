package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.transaction.LocalTransaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dto.LocalTransactionDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.LocalTransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.LocalTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.Currency;

import static com.ironhack.midterm.util.UserUtil.compareUserNames;

@Service
public class LocalTransactionServiceImpl implements LocalTransactionService {

  @Autowired
  private LocalTransactionRepository localTransactionRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public void newTransaction(long accountId, LocalTransactionDTO localTransaction) throws InstanceNotFoundException, IllegalArgumentException {
    Account ownerAccount = accountService.getById(accountId);
    Account targetAccount = accountService.getById(localTransaction.getTargetAccountId());
    AccountHolder accountOwner;

    if (compareUserNames(targetAccount.getPrimaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      accountOwner = ownerAccount.getPrimaryOwner();
    } else if (targetAccount.getSecondaryOwner() != null && compareUserNames(targetAccount.getSecondaryOwner().getName(), localTransaction.getTargetOwnerName())) {
      accountOwner = ownerAccount.getSecondaryOwner();
    } else {
      throw new IllegalArgumentException("Target owner name does not correspond to target account");
    }

    localTransactionRepository.save(
        new LocalTransaction(
            new Money(localTransaction.getTransferValue(), Currency.getInstance(localTransaction.getCurrency())),
            ownerAccount,
            targetAccount,
            accountOwner
        )
    );
  }


}
