package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionPurpose;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.ironhack.midterm.util.MoneyUtil.*;
import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "base_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "base_currency", nullable = false))
  })
  private Money baseAmount;

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "converted_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "converted_currency", nullable = false))
  })
  private Money convertedAmount;

  @JsonIncludeProperties(value = {"id"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "base_account_id")
  private Account baseAccount;

  @JsonIncludeProperties(value = {"id"})
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_account_id")
  private Account targetAccount;

  @JsonIncludeProperties(value = {"id", "name"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_owner_id")
  private AccountHolder targetOwner;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_purpose")
  private TransactionPurpose transactionPurpose;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @NotNull
  @Column(name = "operation_date")
  private LocalDateTime operationDate;


  @JsonIgnore
  @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE})
  private List<Receipt> receipts;


  // ======================================== CONSTRUCTORS ========================================
  // Constructor with target, base accounts and target owner. (For Local Transactions)
  // Set convert amount to adjust to the target account's balance currency, set status(processing),
  // and set operation date(now)
  public Transaction(Money baseAmount, Account baseAccount, Account targetAccount, AccountHolder targetOwner) {
    this.baseAmount = baseAmount;
    this.convertedAmount = convertCurrency(targetAccount.getBalance(), baseAmount);
    this.baseAccount = baseAccount;
    this.targetAccount = targetAccount;
    this.targetOwner = targetOwner;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
  }

  // Constructor with target account and transaction purpose. (For third party transactions)
  // Set convert amount to adjust to the target account's balance currency, set status(processing),
  // and set operation date(now)
  public Transaction(Money baseAmount, Account targetAccount, TransactionPurpose transactionPurpose) {
    this.baseAmount = baseAmount;
    this.convertedAmount = convertCurrency(targetAccount.getBalance(), baseAmount);
    this.targetAccount = targetAccount;
    this.transactionPurpose = transactionPurpose;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
  }

  // Constructor only with target account.
  // Set convert amount to adjust to the target account's balance currency, set status(processing),
  // and set operation date(now)
  public Transaction(Money baseAmount, Account targetAccount) {
    this.baseAmount = baseAmount;
    this.convertedAmount = convertCurrency(targetAccount.getBalance(), baseAmount);
    this.targetAccount = targetAccount;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
  }


  // ======================================== METHODS ========================================

  // ---------- Local Transaction ----------
  // Process THIRD PARTY TRANSACTION and create a receipt for the transaction receiver.
  public Receipt generateLocalTransactionReceiverReceipt(boolean accepted) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    String message = accepted ?
        "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account." :
        "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account.";
    return new Receipt(
        getTargetAccount(),
        getBaseAccount(),
        TransactionType.RECEIVE_FROM_LOCAL,
        accepted ?
            getConvertedAmount() :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process THIRD PARTY TRANSACTION and create a receipt for the transaction sender.
  public Receipt generateLocalTransactionSenderReceipt(boolean accepted) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    String message = accepted ?
        "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account." :
        "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.";
    return new Receipt(
        getBaseAccount(),
        getTargetAccount(),
        TransactionType.SEND_TO_LOCAL,
        accepted ?
            negativeMoney(getConvertedAmount()) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process THIRD PARTY TRANSACTION and create a receipt for the transaction receiver. Custom message.
  public Receipt generateLocalTransactionReceiverReceipt(boolean accepted, String message) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        getBaseAccount(),
        TransactionType.RECEIVE_FROM_LOCAL,
        accepted ?
            getConvertedAmount() :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process THIRD PARTY TRANSACTION and create a receipt for the transaction sender. Custom message.
  public Receipt generateLocalTransactionSenderReceipt(boolean accepted, String message) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    return new Receipt(
        getBaseAccount(),
        getTargetAccount(),
        TransactionType.SEND_TO_LOCAL,
        accepted ?
            negativeMoney(getConvertedAmount()) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ---------- Third Party Transaction ----------
  // Process THIRD PARTY TRANSACTION and create a receipt.
  public Receipt generateThirdPartyTransactionReceipt(boolean accepted) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    String message = accepted ?
        (transactionPurpose == TransactionPurpose.SEND ?
            "The amount of " + getConvertedAmount().toString() + " was successfully transferred to this account." :
            "The amount of " + getConvertedAmount().toString() + " was successfully transferred from this account.") :
        (transactionPurpose == TransactionPurpose.SEND ?
            "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred to this account." :
            "An error occurred! The amount of " + getConvertedAmount().toString() + " was NOT transferred from this account.");
    return new Receipt(
        getTargetAccount(),
        transactionPurpose == TransactionPurpose.SEND ?
            TransactionType.RECEIVE_FROM_THIRD_PARTY :
            TransactionType.SEND_TO_THIRD_PARTY,
        accepted ?
            (transactionPurpose == TransactionPurpose.SEND ? getConvertedAmount() : negativeMoney(getConvertedAmount())) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process THIRD PARTY TRANSACTION and create a receipt. Custom message.
  public Receipt generateThirdPartyTransactionReceipt(boolean accepted, String message) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        transactionPurpose == TransactionPurpose.SEND ?
            TransactionType.RECEIVE_FROM_THIRD_PARTY :
            TransactionType.SEND_TO_THIRD_PARTY,
        accepted ?
            (transactionPurpose == TransactionPurpose.SEND ? getConvertedAmount() : negativeMoney(getConvertedAmount())) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ---------- Interest Transaction ----------
  // Process INTEREST TRANSACTION and create a receipt.
  public Receipt generateInterestTransactionReceipt(boolean accepted) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    String message = accepted ?
        "The interest amount of " + getConvertedAmount().toString() + " was successfully added to this account." :
        "An error occurred! The interest amount of " + getConvertedAmount().toString() + " was NOT added to this account.";
    return new Receipt(
        getTargetAccount(),
        TransactionType.INTEREST,
        accepted ?
            getConvertedAmount() :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process INTEREST TRANSACTION and create a receipt. Custom message.
  public Receipt generateInterestTransactionReceipt(boolean accepted, String message) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.INTEREST,
        accepted ?
            getConvertedAmount() :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ---------- Maintenance Fee Transaction ----------
  // Process MAINTENANCE FEE TRANSACTION and create a receipt.
  public Receipt generateMaintenanceFeeTransactionReceipt(boolean accepted) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    String message = accepted ?
        "The maintenance fee of " + getConvertedAmount().toString() + " was successfully withdrawn from this account." :
        "An error occurred! The maintenance amount of " + getConvertedAmount().toString() + " was NOT withdrawn from this account.";
    return new Receipt(
        getTargetAccount(),
        TransactionType.MAINTENANCE_FEE,
        accepted ?
            negativeMoney(getConvertedAmount()) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process MAINTENANCE FEE TRANSACTION and create a receipt. Custom message.
  public Receipt generateMaintenanceFeeTransactionReceipt(boolean accepted, String message) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.MAINTENANCE_FEE,
        accepted ?
            negativeMoney(getConvertedAmount()) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }


  // ---------- Penalty Fee Transaction ----------
  // Process PENALTY FEE TRANSACTION and create a receipt.
  public Receipt generatePenaltyFeeTransactionReceipt(boolean accepted) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    String message = accepted ?
        "The penalty fee of " + getConvertedAmount().toString() + " was successfully withdrawn from this account." :
        "An error occurred! The penalty fee of " + getConvertedAmount().toString() + " was NOT withdrawn from this account.";
    return new Receipt(
        getTargetAccount(),
        TransactionType.PENALTY_FEE,
        accepted ?
            negativeMoney(getConvertedAmount()) :
            newMoney("0", getConvertedAmount().getCurrency().getCurrencyCode()),
        getStatus(),
        message,
        getOperationDate(),
        this
    );
  }

  // Process PENALTY FEE TRANSACTION and create a receipt. Custom message.
  public Receipt generatePenaltyFeeTransactionReceipt(boolean accepted, String message) {
    setStatus(accepted ? Status.ACCEPTED : Status.REFUSED);
    return new Receipt(
        getTargetAccount(),
        TransactionType.PENALTY_FEE,
        accepted ?
            negativeMoney(getConvertedAmount()) :
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
        "id = " + id + ", " +
        "baseAmount = " + baseAmount + ", " +
        "convertedAmount = " + convertedAmount + ", " +
        (baseAccount != null ?
            "baseAccount = " + baseAccount.getId() + ", " :
            ""
        ) +
        "targetAccount = " + targetAccount.getId() + ", " +
        (this.targetOwner != null ?
            "targetOwner = " + targetOwner.getId() + ": " + targetOwner.getName() + ", " :
            "") +
        (this.transactionPurpose != null ?
            "transactionPurpose = " + transactionPurpose + ", " :
            "") +
        "status = " + status + ", " +
        "operationDate = " + operationDate + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Transaction that = (Transaction) o;
    return getId().equals(that.getId()) && getBaseAmount().equals(that.getBaseAmount()) && getConvertedAmount().equals(that.getConvertedAmount()) && getStatus() == that.getStatus() && getOperationDate().equals(that.getOperationDate());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getBaseAmount(), getConvertedAmount(), getStatus(), getOperationDate());
  }

}
