package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "receipt")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Receipt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  private Account personalAccount;

  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "external_account_id")
  private Account externalAccount;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type")
  private TransactionType transactionType;

  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "base_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "base_currency", nullable = false))
  })
  private Money baseAmount;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @NotNull
  @Column(name = "details")
  private String details;

  @NotNull
  @Column(name = "operation_date")
  private LocalDateTime operationDate;

  @JsonIncludeProperties(value = {"id"})
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id")
  private Transaction transaction;


  // ======================================== CONSTRUCTORS ========================================
  // Constructor with personal and external accounts. (for Local Transactions)
  public Receipt(Account personalAccount, Account externalAccount, TransactionType transactionType, Money baseAmount, Status status, String details, LocalDateTime operationDate, Transaction transaction) {
    this.personalAccount = personalAccount;
    this.externalAccount = externalAccount;
    this.transactionType = transactionType;
    this.baseAmount = baseAmount;
    this.status = status;
    this.details = details;
    this.operationDate = operationDate;
    this.transaction = transaction;
  }

  // Constructor only with personal account.
  public Receipt(Account personalAccount, TransactionType transactionType, Money baseAmount, Status status, String details, LocalDateTime operationDate, Transaction transaction) {
    this.personalAccount = personalAccount;
    this.transactionType = transactionType;
    this.baseAmount = baseAmount;
    this.status = status;
    this.details = details;
    this.operationDate = operationDate;
    this.transaction = transaction;
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "personalAccount = " + personalAccount.getId() + ", " +
        (externalAccount != null ?
            "externalAccount = " + externalAccount.getId() + ", " :
            ""
        ) +
        "transactionType = " + transactionType + ", " +
        "baseAmount = " + baseAmount + ", " +
        "status = " + status + ", " +
        "details = " + details + ", " +
        "operationDate = " + operationDate + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Receipt receipt = (Receipt) o;
    return getId().equals(receipt.getId()) && getPersonalAccount().equals(receipt.getPersonalAccount()) && Objects.equals(getExternalAccount(), receipt.getExternalAccount()) && getTransactionType() == receipt.getTransactionType() && getBaseAmount().equals(receipt.getBaseAmount()) && getStatus() == receipt.getStatus() && getDetails().equals(receipt.getDetails()) && getOperationDate().equals(receipt.getOperationDate()) && getTransaction().equals(receipt.getTransaction());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPersonalAccount(), getExternalAccount(), getTransactionType(), getBaseAmount(), getStatus(), getDetails(), getOperationDate(), getTransaction());
  }

}
