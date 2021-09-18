package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "local_transfer")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocalTransfer extends Transaction {

  @NotNull
  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_account_id")
  private Account account;

  @NotNull
  @JsonIncludeProperties(value = {"id", "name"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private AccountHolder owner;

  @NotNull
  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_account_id")
  private Account targetAccount;

  @NotNull
  @JsonIncludeProperties(value = {"id", "name"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_owner_id")
  private AccountHolder targetOwner;


  // ======================================== CONSTRUCTORS ========================================
  public LocalTransfer(Money baseAmount, Money convertedAmount, Status status, Account account, AccountHolder owner, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount, convertedAmount, status);
    this.account = account;
    this.owner = owner;
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
  }

  public LocalTransfer(Money baseAmount, Status status, Account account, AccountHolder owner, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount, status);
    this.account = account;
    this.owner = owner;
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================

}
