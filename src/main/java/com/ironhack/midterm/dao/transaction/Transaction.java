package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.enums.Status;
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

import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;
import static com.ironhack.midterm.util.validation.DateTimeUtil.dateTimeNow;

@Entity
@Table(name = "transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Transaction {

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

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @NotNull
  @Column(name = "operation_date")
  private LocalDateTime operationDate;

  @JsonIgnore
  @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE})
  private List<TransactionReceipt> receipts;


  // ======================================== CONSTRUCTORS ========================================
  public Transaction(Money baseAmount, Account baseAccount, Account targetAccount) {
    this.baseAmount = baseAmount;
    this.convertedAmount = convertCurrency(targetAccount.getBalance(), baseAmount);
    this.baseAccount = baseAccount;
    this.targetAccount = targetAccount;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
  }

  public Transaction(Money baseAmount, Account targetAccount) {
    this.baseAmount = baseAmount;
    this.convertedAmount = convertCurrency(targetAccount.getBalance(), baseAmount);
    this.targetAccount = targetAccount;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
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
