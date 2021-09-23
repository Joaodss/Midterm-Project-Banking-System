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

import static com.ironhack.midterm.util.MoneyUtil.newMoney;
import static com.ironhack.midterm.util.validation.DateTimeUtil.dateTimeNow;

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private AccountType accountType;


  @Valid
  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "balance_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "balance_currency", nullable = false))
  })
  private Money balance;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "primary_owner_id")
  @JsonIncludeProperties(value = {"id", "name"})
  private AccountHolder primaryOwner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "secondary_owner_id")
  @JsonIncludeProperties(value = {"id", "name"})
  private AccountHolder secondaryOwner;

  @Valid
  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "penaltyFee_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "penaltyFee_currency", nullable = false))
  })
  private Money penaltyFee;

  @NotNull
  @Column(name = "last_penalty_fee")
  private LocalDate lastPenaltyFee;

  @NotNull
  @Column(name = "creation_date")
  private LocalDateTime creationDate;

  // ======================================== MAPPING ========================================

  @OneToMany(mappedBy = "targetAccount")
  @JsonIgnore
  @ToString.Exclude
  private List<Transaction> incomingTransactions = new ArrayList<>();

  @OneToMany(mappedBy = "baseAccount")
  @JsonIgnore
  @ToString.Exclude
  private List<Transaction> outgoingTransactions = new ArrayList<>();


  // ======================================== CONSTRUCTORS ========================================
  public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.secondaryOwner = secondaryOwner;
    this.penaltyFee = newMoney("40");
    this.creationDate = dateTimeNow();
    this.lastPenaltyFee = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
  }

  public Account(Money balance, AccountHolder primaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.penaltyFee = newMoney("40");
    this.creationDate = dateTimeNow();
    this.lastPenaltyFee = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
  }


  // ======================================== METHODS ========================================
  public List<Transaction> getAllTransactionsOrdered() {
    List<Transaction> allTransactions = new ArrayList<>();
    allTransactions.addAll(incomingTransactions);
    allTransactions.addAll(outgoingTransactions);
    allTransactions.sort(Comparator.comparing(Transaction::getOperationDate).reversed());
    return allTransactions;
  }


  // ======================================== OVERRIDE METHODS ========================================


}
