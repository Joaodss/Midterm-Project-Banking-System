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
import java.util.Objects;

import static com.ironhack.midterm.util.MoneyUtil.negativeMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "local_transaction")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocalTransaction extends Transaction {

  @JsonIncludeProperties(value = {"id", "name"})
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_owner_id")
  private AccountHolder targetOwner;


  // ======================================== CONSTRUCTORS ========================================
  public LocalTransaction(Money baseAmount, Account baseAccount, Account targetAccount, AccountHolder targetOwner) {
    super(baseAmount, baseAccount, targetAccount);
    this.targetOwner = targetOwner;
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceiverReceipt() {
    setStatus(Status.ACCEPTED);
    return new TransactionReceipt(
        getTargetAccount(),
        getBaseAccount(),
        TransactionType.RECEIVE_LOCAL,
        getConvertedAmount(),
        getStatus(),
        "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account.",
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt acceptAndGenerateSenderReceipt() {
    setStatus(Status.ACCEPTED);
    return new TransactionReceipt(
        getBaseAccount(),
        getTargetAccount(),
        TransactionType.SEND_LOCAL,
        negativeMoney(getConvertedAmount()),
        getStatus(),
        "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account.",
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceiverReceipt() {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getTargetAccount(),
        getBaseAccount(),
        TransactionType.RECEIVE_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account.",
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateSenderReceipt() {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getBaseAccount(),
        getTargetAccount(),
        TransactionType.SEND_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.",
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceiverReceipt(String message) {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getTargetAccount(),
        getBaseAccount(),
        TransactionType.RECEIVE_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateSenderReceipt(String message) {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getBaseAccount(),
        getTargetAccount(),
        TransactionType.SEND_LOCAL,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "super = " + super.toString() + ", " +
        "targetOwner = " + targetOwner.getId() + ": " + targetOwner.getName() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LocalTransaction that = (LocalTransaction) o;
    return getTargetOwner().equals(that.getTargetOwner());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getTargetOwner());
  }

}
