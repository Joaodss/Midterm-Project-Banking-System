package com.ironhack.midterm.service.transaction.impl;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.account.CheckingAccount;
import com.ironhack.midterm.dao.account.SavingsAccount;
import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import com.ironhack.midterm.dao.transaction.ThirdPartyTransaction;
import com.ironhack.midterm.dto.ThirdPartyTransactionDTO;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.repository.transaction.ThirdPartyTransactionRepository;
import com.ironhack.midterm.service.account.AccountService;
import com.ironhack.midterm.service.transaction.ThirdPartyTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.Currency;

import static com.ironhack.midterm.util.TransactionPurposeUtil.transactionPurposeFromString;

@Service
public class ThirdPartyTransactionServiceImpl implements ThirdPartyTransactionService {

  @Autowired
  private ThirdPartyTransactionRepository thirdPartyTransactionRepository;

  @Autowired
  private AccountService accountService;


  // ======================================== ADD TRANSACTION Methods ========================================
  public void newTransaction(ThirdPartyTransactionDTO thirdPartyTransaction) throws InstanceNotFoundException, IllegalArgumentException {
    Account targetAccount = accountService.getById(thirdPartyTransaction.getTargetAccountId());
    boolean isValidKey;

    if (targetAccount.getClass() == CheckingAccount.class) {
      isValidKey = (((CheckingAccount) targetAccount).getSecretKey().equals(thirdPartyTransaction.getSecretKey()));
    } else if (targetAccount.getClass() == StudentCheckingAccount.class) {
      isValidKey = (((StudentCheckingAccount) targetAccount).getSecretKey().equals(thirdPartyTransaction.getSecretKey()));
    } else if (targetAccount.getClass() == SavingsAccount.class) {
      isValidKey = (((SavingsAccount) targetAccount).getSecretKey().equals(thirdPartyTransaction.getSecretKey()));
    } else {
      throw new IllegalArgumentException("Account not valid for third party transactions.");
    }
    if (!isValidKey) throw new IllegalArgumentException("Account key is not valid for the targeted account.");

    thirdPartyTransactionRepository.save(
        new ThirdPartyTransaction(
            new Money(thirdPartyTransaction.getTransferValue(), Currency.getInstance(thirdPartyTransaction.getCurrency())),
            targetAccount,
            thirdPartyTransaction.getSecretKey(),
            transactionPurposeFromString(thirdPartyTransaction.getTransactionPurpose())
        )
    );
  }


}
