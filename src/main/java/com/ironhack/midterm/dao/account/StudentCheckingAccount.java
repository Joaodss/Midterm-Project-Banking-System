package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Money;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "student_checking_account")
@PrimaryKeyJoinColumn(name = "id")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StudentCheckingAccount extends CheckingAccount {


  // ======================================== Constructors ========================================
  // ==================== Constructors with default creditLimit/interestRate ====================
  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey) {
    super(balance, primaryOwner, secondaryOwner, secretKey);
    setMinimumBalance(new Money(new BigDecimal("0.00")));
    setPenaltyFee(new Money(new BigDecimal("0.00")));
    setMonthlyMaintenanceFee(new Money(new BigDecimal("0.00")));
  }

  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
    super(balance, primaryOwner, secretKey);
    setMinimumBalance(new Money(new BigDecimal("0.00")));
    setPenaltyFee(new Money(new BigDecimal("0.00")));
    setMonthlyMaintenanceFee(new Money(new BigDecimal("0.00")));
  }
}
