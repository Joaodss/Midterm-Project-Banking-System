package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static com.ironhack.midterm.util.validation.DateTimeUtil.dateTimeNow;

@Entity
@Table(name = "deposit")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Deposit extends Transaction {

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
  public Deposit(Money baseAmount, Money convertedAmount, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount, convertedAmount);
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
  }

  public Deposit(Money baseAmount, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount);
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    String message = "The amount of " + getConvertedAmount().toString() + " was successfully deposited.";
    return new TransactionReceipt(
        getTargetAccount(),
        TransactionType.DEPOSIT,
        getConvertedAmount(),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    String message = "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT deposited.";
    return new TransactionReceipt(
        getTargetAccount(),
        TransactionType.DEPOSIT,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(), message,
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceipt(String message) {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getTargetAccount(),
        TransactionType.DEPOSIT,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(), message,
        getOperationDate(),
        this
    );
  }


  // ======================================== OVERRIDE METHODS ========================================


}
