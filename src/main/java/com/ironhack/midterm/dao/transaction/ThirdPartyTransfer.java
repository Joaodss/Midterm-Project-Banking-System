package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionPurpose;
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
@Table(name = "third_party_transaction")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThirdPartyTransfer extends Transaction {

  @NotNull
  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_account_id")
  private Account targetAccount;

  @NotNull
  @Column(name = "secret_key")
  private String secretKey;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "transactionPurpose")
  private TransactionPurpose transactionPurpose;


  // ======================================== CONSTRUCTORS ========================================
  public ThirdPartyTransfer(Money baseAmount, Money convertedAmount, Account targetAccount, String secretKey, TransactionPurpose transactionPurpose) {
    super(baseAmount, convertedAmount);
    this.targetAccount = targetAccount;
    this.secretKey = secretKey;
    this.transactionPurpose = transactionPurpose;
  }

  public ThirdPartyTransfer(Money baseAmount, Account targetAccount, String secretKey, TransactionPurpose transactionPurpose) {
    super(baseAmount);
    this.targetAccount = targetAccount;
    this.secretKey = secretKey;
    this.transactionPurpose = transactionPurpose;
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    if (transactionPurpose == TransactionPurpose.RECEIVE) {
      String message = "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account.";
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.RECEIVE_THIRD_PARTY,
          getConvertedAmount(),
          getStatus(),
          message,
          dateTimeNow(),
          this
      );
    } else {
      String message = "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account.";
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.SEND_THIRD_PARTY,
          negativeMoney(getConvertedAmount()),
          getStatus(),
          message,
          dateTimeNow(),
          this
      );
    }
  }

  public TransactionReceipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    if (transactionPurpose == TransactionPurpose.RECEIVE) {
      String message = "The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account.";
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.RECEIVE_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          dateTimeNow(),
          this
      );
    } else {
      String message = "The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.";
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.SEND_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          dateTimeNow(),
          this
      );
    }
  }

  public TransactionReceipt refuseAndGenerateReceipt(String message) {
    setStatus(Status.REFUSED);
    if (transactionPurpose == TransactionPurpose.RECEIVE) {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.RECEIVE_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          dateTimeNow(),
          this
      );
    } else {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.SEND_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          dateTimeNow(),
          this
      );
    }
  }


  // ======================================== OVERRIDE METHODS ========================================


}
