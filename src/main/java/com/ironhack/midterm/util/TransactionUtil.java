package com.ironhack.midterm.util;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionUtil {

  public static boolean isTransactionBalanceValid(Money amount, Account account) {
    Money convertedAmount = convertCurrency(account.getBalance(), amount);
    return account.getBalance().getAmount().compareTo(convertedAmount.getAmount()) >= 0;
  }


}
