package com.ironhack.midterm.dao.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.util.validation.CreditLimitConstrain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.ironhack.midterm.util.helper.MoneyHelper.newMoney;

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Valid
  @NotNull
  @CreditLimitConstrain
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "balance_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "balance_currency", nullable = false))
  })
  private Money balance;

  @JsonIgnoreProperties(value = {"username", "password", "primaryAccounts", "secondaryAccounts"}, allowSetters = true)
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "primary_owner_id")
  private AccountHolder primaryOwner;

  @JsonIgnoreProperties(value = {"username", "password", "primaryAccounts", "secondaryAccounts"}, allowSetters = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "secondary_owner_id")
  private AccountHolder secondaryOwner;

  @Valid
  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "penaltyFee_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "penaltyFee_currency", nullable = false))
  })
  private Money penaltyFee;


  // ======================================== Constructors ========================================
  // ==================== Constructors with default penaltyFee ====================
  public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.secondaryOwner = secondaryOwner;
    this.penaltyFee = newMoney("40.00");
  }

  public Account(Money balance, AccountHolder primaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.secondaryOwner = null;
    this.penaltyFee = newMoney("40.00");
  }


  // ======================================== Getters & Setters ========================================


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return getId().equals(account.getId()) && getBalance().equals(account.getBalance()) && getPrimaryOwner().equals(account.getPrimaryOwner()) && Objects.equals(getSecondaryOwner(), account.getSecondaryOwner()) && getPenaltyFee().equals(account.getPenaltyFee());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getBalance(), getPrimaryOwner(), getSecondaryOwner(), getPenaltyFee());
  }

  @Override
  public String toString() {
    return "Account{" +
        "id=" + id +
        ", balance=" + balance +
        ", primaryOwner=" + primaryOwner.getId() + ": " + primaryOwner.getName() +
        ", secondaryOwner=" + secondaryOwner.getId() + ": " + secondaryOwner.getName() +
        ", penaltyFee=" + penaltyFee +
        '}';
  }

}
