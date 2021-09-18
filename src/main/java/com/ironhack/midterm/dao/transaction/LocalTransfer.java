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

import static com.ironhack.midterm.util.MoneyUtil.negativeMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static com.ironhack.midterm.util.validation.DateTimeUtil.dateTimeNow;

@Entity
@Table(name = "local_transaction")
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
  public LocalTransfer(Money baseAmount, Money convertedAmount, Account account, AccountHolder owner, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount, convertedAmount);
    this.account = account;
    this.owner = owner;
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
  }

  public LocalTransfer(Money baseAmount, Account account, AccountHolder owner, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount);
    this.account = account;
    this.owner = owner;
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceiverReceipt() {
    setStatus(Status.ACCEPTED);
    String message = "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account.";
    return new TransactionReceipt(
        getTargetAccount(),
        getAccount(),
        TransactionType.RECEIVE_LOCAL,
        getConvertedAmount(),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt acceptAndGenerateSenderReceipt() {
    setStatus(Status.ACCEPTED);
    String message = "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account.";
    return new TransactionReceipt(
        getAccount(),
        getTargetAccount(),
        TransactionType.SEND_LOCAL,
        negativeMoney(getConvertedAmount()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceiverReceipt() {
    setStatus(Status.REFUSED);
    String message = "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account.";
    return new TransactionReceipt(
        getTargetAccount(),
        getAccount(),
        TransactionType.RECEIVE_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateSenderReceipt() {
    setStatus(Status.REFUSED);
    String message = "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.";
    return new TransactionReceipt(
        getAccount(),
        getTargetAccount(),
        TransactionType.SEND_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceiverReceipt(String message) {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getTargetAccount(),
        getAccount(),
        TransactionType.RECEIVE_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateSenderReceipt(String message) {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getAccount(),
        getTargetAccount(),
        TransactionType.SEND_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }



  // ======================================== OVERRIDE METHODS ========================================

}
