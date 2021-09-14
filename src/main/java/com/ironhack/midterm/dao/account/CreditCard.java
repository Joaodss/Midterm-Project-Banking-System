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

@Entity
@Table(name = "credit_card_account")
@PrimaryKeyJoinColumn(name = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
    this.creditLimit = new Money(new BigDecimal("100.00"));
    this.interestRate = new BigDecimal("0.20");
  }

  public CreditCard(Money balance, AccountHolder primaryOwner) {
    super(balance, primaryOwner);
    this.creditLimit = new Money(new BigDecimal("100.00"));
    this.interestRate = new BigDecimal("0.20");
  }


}
