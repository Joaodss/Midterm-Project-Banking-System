package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.model.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.security.NoSuchAlgorithmException;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "student_checking_account")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@Getter
@Setter
public class StudentCheckingAccount extends CheckingAccount {


  // ======================================== CONSTRUCTORS ========================================
  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner, secondaryOwner);
    super.setAccountType(AccountType.STUDENT_CHECKING_ACCOUNT);
    setMinimumBalance(newMoney("0"));
    setMonthlyMaintenanceFee(newMoney("0"));
    setPenaltyFee(newMoney("0"));
  }

  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner);
    super.setAccountType(AccountType.STUDENT_CHECKING_ACCOUNT);
    setMinimumBalance(newMoney("0"));
    setMonthlyMaintenanceFee(newMoney("0"));
    setPenaltyFee(newMoney("0"));
  }


  // ======================================== METHODS ========================================
  public void updateCurrencyValues() {
    setPenaltyFee(newMoney("0", getBalance().getCurrency().getCurrencyCode()));
    setMinimumBalance(newMoney("0", getBalance().getCurrency().getCurrencyCode()));
    setMonthlyMaintenanceFee(newMoney("0", getBalance().getCurrency().getCurrencyCode()));
  }

  // ======================================== OVERRIDE METHODS ========================================


}
