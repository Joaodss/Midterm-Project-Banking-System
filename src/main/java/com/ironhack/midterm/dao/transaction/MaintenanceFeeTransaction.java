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

import static com.ironhack.midterm.util.MoneyUtil.negativeMoney;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "maintenance_fee_transaction")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class MaintenanceFeeTransaction extends Transaction {

  // ======================================== CONSTRUCTORS ========================================
  public MaintenanceFeeTransaction(Money baseAmount, Account targetAccount) {
    super(baseAmount, targetAccount);
  }


  // ======================================== METHODS ========================================
  // Approve transaction and create a receipt.
  public Receipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.MAINTENANCE_FEE,
        negativeMoney(getConvertedAmount()),
        getStatus(),
        "The maintenance fee of " + getConvertedAmount().toString() + " was withdrawn from this account.",
        getOperationDate(),
        this
    );
  }

  // Refuse transaction and create a receipt.
  public Receipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.MAINTENANCE_FEE,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        "An error occurred! The maintenance amount of " + getConvertedAmount().toString() + " was withdrawn from this account.",
        getOperationDate(),
        this
    );
  }

  // Refuse transaction and create a receipt (custom message).
  public Receipt refuseAndGenerateReceipt(String message) {
    setStatus(Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.MAINTENANCE_FEE,
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
