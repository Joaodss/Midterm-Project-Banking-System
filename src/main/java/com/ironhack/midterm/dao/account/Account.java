package com.ironhack.midterm.dao.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.transaction.Transaction;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.model.Money;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.ironhack.midterm.util.DateTimeUtil.dateTimeNow;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "account_type")
  private AccountType accountType;

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "balance_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "balance_currency", nullable = false))
  })
  private Money balance;

  @JsonIncludeProperties(value = {"id", "name"})
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "primary_owner_id")
  private AccountHolder primaryOwner;

  @JsonIncludeProperties(value = {"id", "name"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "secondary_owner_id")
  private AccountHolder secondaryOwner;

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "penaltyFee_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "penaltyFee_currency", nullable = false))
  })
  private Money penaltyFee;

  @NotNull
  @Column(name = "last_penalty_fee_check")
  private LocalDate lastPenaltyFeeCheck;

  @NotNull
  @Column(name = "creation_date")
  private LocalDateTime creationDate;

  @JsonIgnore
  @ToString.Exclude
  @OneToMany(mappedBy = "targetAccount")
  private List<Transaction> incomingTransactions = new ArrayList<>();

  @JsonIgnore
  @ToString.Exclude
  @OneToMany(mappedBy = "baseAccount")
  private List<Transaction> outgoingTransactions = new ArrayList<>();


  // ======================================== CONSTRUCTORS ========================================
  // Constructor with primary and secondary owners.
  // Set default values for creationDate(now), penaltyFee(40 €), and lastPenaltyFeeCheck(start of the month).
  public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.secondaryOwner = secondaryOwner;
    this.penaltyFee = newMoney("40.00");
    this.creationDate = dateTimeNow();
    this.lastPenaltyFeeCheck = getCreationDate().toLocalDate().withDayOfMonth(1);
  }

  // Constructor only with primary owner.
  // Set default values for creationDate(now), penaltyFee(40 €), and lastPenaltyFeeCheck(start of the month).
  public Account(Money balance, AccountHolder primaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.penaltyFee = newMoney("40.00");
    this.creationDate = dateTimeNow();
    this.lastPenaltyFeeCheck = getCreationDate().toLocalDate().withDayOfMonth(1);
  }


  // ======================================== METHODS ========================================
  // Joins incomingTransactions and outgoingTransactions, and returns them ordered by date (new on top).
  @JsonIgnore
  public List<Transaction> getAllTransactionsOrdered() {
    List<Transaction> allTransactions = new ArrayList<>();
    allTransactions.addAll(incomingTransactions);
    allTransactions.addAll(outgoingTransactions);
    allTransactions.sort(Comparator.comparing(Transaction::getOperationDate).reversed());
    return allTransactions;
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "accountType = " + accountType + ", " +
        "balance = " + balance + ", " +
        "primaryOwner = " + primaryOwner.getId() + ": " + primaryOwner.getUsername() + ", " +
        (secondaryOwner != null ?
            "secondaryOwner = " + secondaryOwner.getId() + ": " + secondaryOwner.getUsername() + ", " :
            ""
        ) +
        "penaltyFee = " + penaltyFee + ", " +
        "lastPenaltyFeeCheck = " + lastPenaltyFeeCheck + ", " +
        "creationDate = " + creationDate + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return getId().equals(account.getId()) && getAccountType() == account.getAccountType() && getBalance().equals(account.getBalance()) && getPenaltyFee().equals(account.getPenaltyFee()) && getLastPenaltyFeeCheck().equals(account.getLastPenaltyFeeCheck()) && getCreationDate().equals(account.getCreationDate());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getAccountType(), getBalance(), getPenaltyFee(), getLastPenaltyFeeCheck(), getCreationDate());
  }

}
