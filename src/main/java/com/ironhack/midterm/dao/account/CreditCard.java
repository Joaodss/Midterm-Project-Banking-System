package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.model.Money;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "credit_card_account")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CreditCard extends Account {

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency", nullable = false))
  })
  private Money creditLimit;

  @NotNull
  @Column(name = "interest_rate")
  private BigDecimal interestRate;

  @NotNull
  @Column(name = "last_interest_update")
  private LocalDate lastInterestUpdate;


  // ======================================== CONSTRUCTORS ========================================
  public CreditCard(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    super(balance, primaryOwner, secondaryOwner);
    super.setAccountType(AccountType.CREDIT_CARD);
    this.creditLimit = newMoney("100.00");
    this.interestRate = new BigDecimal("0.2");
    this.lastInterestUpdate = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
  }

  public CreditCard(Money balance, AccountHolder primaryOwner) {
    super(balance, primaryOwner);
    super.setAccountType(AccountType.CREDIT_CARD);
    this.creditLimit = newMoney("100.00");
    this.interestRate = new BigDecimal("0.2");
    this.lastInterestUpdate = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
  }


  // ======================================== METHODS ========================================
  public void updateCurrencyValues() {
    setPenaltyFee(convertCurrency(getBalance(), getPenaltyFee()));
    setCreditLimit(convertCurrency(getBalance(), getCreditLimit()));
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CreditCard that = (CreditCard) o;
    return getCreditLimit().equals(that.getCreditLimit()) && getInterestRate().equals(that.getInterestRate()) && getLastInterestUpdate().equals(that.getLastInterestUpdate());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getCreditLimit(), getInterestRate(), getLastInterestUpdate());
  }

}
