package com.ironhack.midterm.dao.transaction;

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

@Entity
@Table(name = "third_party_transaction")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThirdPartyTransaction extends Transaction {

  @NotNull
  @Column(name = "secret_key")
  private String secretKey;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "transactionPurpose")
  private TransactionPurpose transactionPurpose;


  // ======================================== CONSTRUCTORS ========================================
  public ThirdPartyTransaction(Money baseAmount, Account account, Account targetAccount, String secretKey, TransactionPurpose transactionPurpose) {
    super(baseAmount, account, targetAccount);
    this.secretKey = secretKey;
    this.transactionPurpose = transactionPurpose;
  }

  public ThirdPartyTransaction(Money baseAmount, Account targetAccount, String secretKey, TransactionPurpose transactionPurpose) {
    super(baseAmount, targetAccount);
    this.secretKey = secretKey;
    this.transactionPurpose = transactionPurpose;
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    if (transactionPurpose == TransactionPurpose.RECEIVE) {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.RECEIVE_THIRD_PARTY,
          getConvertedAmount(),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account.",
          getOperationDate(),
          this
      );
    } else {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.SEND_THIRD_PARTY,
          negativeMoney(getConvertedAmount()),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account.",
          getOperationDate(),
          this
      );
    }
  }

  public TransactionReceipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    if (transactionPurpose == TransactionPurpose.RECEIVE) {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.RECEIVE_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account.",
          getOperationDate(),
          this
      );
    } else {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.SEND_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.",
          getOperationDate(),
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
          getOperationDate(),
          this
      );
    } else {
      return new TransactionReceipt(
          getTargetAccount(),
          TransactionType.SEND_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          getOperationDate(),
          this
      );
    }
  }


  // ======================================== OVERRIDE METHODS ========================================

}
