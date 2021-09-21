package com.ironhack.midterm.dao.transaction;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.model.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "deposit")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor
@Getter
@Setter
public class Deposit extends Transaction {


  // ======================================== CONSTRUCTORS ========================================
  public Deposit(Money baseAmount, Account account, Account targetAccount) {
    super(baseAmount, account, targetAccount);
  }

  public Deposit(Money baseAmount, Account targetAccount) {
    super(baseAmount, targetAccount);
  }


  // ======================================== METHODS ========================================
  public TransactionReceipt acceptAndGenerateReceipt() {
    setStatus(Status.ACCEPTED);
    return new TransactionReceipt(
        getTargetAccount(),
        TransactionType.DEPOSIT,
        getConvertedAmount(),
        getStatus(),
        "The amount of " + getConvertedAmount().toString() + " was successfully deposited.",
        getOperationDate(),
        this
    );
  }

  public TransactionReceipt refuseAndGenerateReceipt() {
    setStatus(Status.REFUSED);
    return new TransactionReceipt(
        getTargetAccount(),
        TransactionType.DEPOSIT,
        newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT deposited.",
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
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ======================================== OVERRIDE METHODS ========================================


}
