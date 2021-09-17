package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newMoney;

@Entity
@Table(name = "student_checking_account")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class StudentCheckingAccount extends CheckingAccount {


  // ======================================== Constructors ========================================
  // ==================== Constructors with default creditLimit/interestRate ====================
  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey) {
    super(balance, primaryOwner, secondaryOwner, secretKey);
    setMinimumBalance(newMoney("0"));
    setPenaltyFee(newMoney("0"));
    setMonthlyMaintenanceFee(newMoney("0"));
  }

  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
    super(balance, primaryOwner, secretKey);
    setMinimumBalance(newMoney("0"));
    setPenaltyFee(newMoney("0"));
    setMonthlyMaintenanceFee(newMoney("0"));
  }


  // ======================================== Getters & Setters ========================================
  @Override
  public void setPenaltyFee(Money penaltyFee) {
    super.setPenaltyFee(newMoney("0"));
  }

  @Override
  public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
    super.setMonthlyMaintenanceFee(newMoney("0"));
  }

  @Override
  public void setMinimumBalance(Money minimumBalance) {
    super.setMinimumBalance(newMoney("0"));
  }


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
