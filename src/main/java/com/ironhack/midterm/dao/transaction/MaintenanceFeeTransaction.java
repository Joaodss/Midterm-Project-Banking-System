package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
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
@Table(name = "maintenance_fee_transaction")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MaintenanceFeeTransaction extends Transaction {

  @NotNull
  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  private Account account;

  // ======================================== CONSTRUCTORS ========================================
  public MaintenanceFeeTransaction(Money baseAmount, Money convertedAmount, Account account) {
    super(baseAmount, convertedAmount);
    this.account = account;
  }

  public MaintenanceFeeTransaction(Money baseAmount, Account account) {
    super(baseAmount);
    this.account = account;
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    String message = "The maintenance fee of " + getConvertedAmount().toString() + " was withdrawn from this account.";
    return new TransactionReceipt(
        getAccount(),
        TransactionType.MAINTENANCE_FEE,
        negativeMoney(getConvertedAmount()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    String message = "An error occurred! The maintenance amount of " + getConvertedAmount().toString() + " was withdrawn from this account.";
    return new TransactionReceipt(
        getAccount(),
        TransactionType.MAINTENANCE_FEE,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceipt(String message) {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getAccount(),
        TransactionType.MAINTENANCE_FEE,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        dateTimeNow(),
        this
    );
  }


  // ======================================== OVERRIDE METHODS ========================================

}
