package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "student_checking_account")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@Getter
@Setter
public class StudentCheckingAccount extends CheckingAccount {


  // ======================================== CONSTRUCTORS ========================================
  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    super(balance, primaryOwner, secondaryOwner);
  }

  public StudentCheckingAccount(Money balance, AccountHolder primaryOwner) {
    super(balance, primaryOwner);
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
