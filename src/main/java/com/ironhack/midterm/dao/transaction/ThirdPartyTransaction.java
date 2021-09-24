package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.model.Money;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.ironhack.midterm.util.MoneyUtil.negativeMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "third_party_transaction")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ThirdPartyTransaction extends Transaction {

  @JsonIgnore
  @ToString.Exclude
  @NotNull
  @Column(name = "secret_key")
  private String secretKey;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "transactionPurpose")
  private TransactionPurpose transactionPurpose;


  // ======================================== CONSTRUCTORS ========================================
  public ThirdPartyTransaction(Money baseAmount, Account targetAccount, String secretKey, TransactionPurpose transactionPurpose) {
    super(baseAmount, targetAccount);
    this.secretKey = secretKey;
    this.transactionPurpose = transactionPurpose;
  }


  // ======================================== METHODS ========================================
  // Approve transaction and create a receipt.
  public Receipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    if (transactionPurpose == TransactionPurpose.SEND) {
      return new Receipt(
          getTargetAccount(),
          TransactionType.RECEIVE_FROM_THIRD_PARTY,
          getConvertedAmount(),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account.",
          getOperationDate(),
          this
      );
    } else {
      return new Receipt(
          getTargetAccount(),
          TransactionType.SEND_TO_THIRD_PARTY,
          negativeMoney(getConvertedAmount()),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account.",
          getOperationDate(),
          this
      );
    }
  }

  // Refuse transaction and create a receipt.
  public Receipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    if (transactionPurpose == TransactionPurpose.SEND) {
      return new Receipt(
          getTargetAccount(),
          TransactionType.RECEIVE_FROM_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account.",
          getOperationDate(),
          this
      );
    } else {
      return new Receipt(
          getTargetAccount(),
          TransactionType.SEND_TO_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          "The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.",
          getOperationDate(),
          this
      );
    }
  }

  // Refuse transaction and create a receipt (custom message).
  public Receipt refuseAndGenerateReceipt(String message) {
    setStatus(Status.REFUSED);
    if (transactionPurpose == TransactionPurpose.SEND) {
      return new Receipt(
          getTargetAccount(),
          TransactionType.RECEIVE_FROM_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          getOperationDate(),
          this
      );
    } else {
      return new Receipt(
          getTargetAccount(),
          TransactionType.SEND_TO_THIRD_PARTY,
          newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
          getStatus(),
          message,
          getOperationDate(),
          this
      );
    }
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThirdPartyTransaction that = (ThirdPartyTransaction) o;
    return getSecretKey().equals(that.getSecretKey()) && getTransactionPurpose() == that.getTransactionPurpose();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSecretKey(), getTransactionPurpose());
  }

}
