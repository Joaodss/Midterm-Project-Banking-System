package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.util.validation.CreditLimitConstrain;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newBD;
import static com.ironhack.midterm.util.money.MoneyInitializerUtil.newMoney;

@Entity
@Table(name = "credit_card_account")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CreditCard extends Account {

  @Valid
  @NotNull
  @CreditLimitConstrain
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency", nullable = false))
  })
  private Money creditLimit;

  @NotNull
  @Digits(integer = 1, fraction = 4)
  @DecimalMin(value = "0.1000")
  private BigDecimal interestRate;


  // ======================================== Constructors ========================================
  // ==================== Constructors with default creditLimit/interestRate ====================
  public CreditCard(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    super(balance, primaryOwner, secondaryOwner);
    this.creditLimit = newMoney("100.00");
    this.interestRate = newBD("0.20", 4);
  }

  public CreditCard(Money balance, AccountHolder primaryOwner) {
    super(balance, primaryOwner);
    this.creditLimit = newMoney("100.00");
    this.interestRate = newBD("0.20", 4);
  }


  // ======================================== Getters & Setters ========================================
  public void setCreditLimit(Money creditLimit) {
    if (creditLimit.getAmount().compareTo(newBD("1000")) > 0)
      throw new IllegalArgumentException("Invalid credit limit amount. Must be equal or lesser than 1000€.");
    this.creditLimit = creditLimit;
  }

  public void setInterestRate(BigDecimal interestRate) {
    if (interestRate.compareTo(newBD("0.1")) < 0)
      throw new IllegalArgumentException("Invalid interest rate amount. Must be equal or greater than 0.1.");
    this.interestRate = interestRate.setScale(4, RoundingMode.HALF_EVEN);
  }


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CreditCard that = (CreditCard) o;
    return getCreditLimit().equals(that.getCreditLimit()) && getInterestRate().equals(that.getInterestRate());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getCreditLimit(), getInterestRate());
  }

}
