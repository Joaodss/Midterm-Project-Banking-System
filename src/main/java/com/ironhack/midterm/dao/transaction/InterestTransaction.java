package com.ironhack.midterm.dao.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.model.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "interest_transaction")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class InterestTransaction extends Transaction {

  // ======================================== CONSTRUCTORS ========================================
  public InterestTransaction(Money baseAmount, Account targetAccount) {
    super(baseAmount, targetAccount);
  }

  // ======================================== METHODS ========================================
  // Approve transaction and create a receipt.
  public Receipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.INTEREST,
        getConvertedAmount(),
        getStatus(),
        "The interest amount of " + getConvertedAmount().toString() + " was successfully added to the account.",
        getOperationDate(),
        this
    );
  }

  // Refuse transaction and create a receipt.
  public Receipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.INTEREST,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        "An error occurred! The interest amount of " + getConvertedAmount().toString() + " was NOT added to the account.",
        getOperationDate(),
        this
    );
  }

  // Refuse transaction and create a receipt (custom message).
  public Receipt refuseAndGenerateReceipt(String message) {
    setStatus(Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.INTEREST,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
