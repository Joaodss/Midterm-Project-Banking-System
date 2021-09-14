package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.util.validation.CreditLimitConstrain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
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

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "primary_owner_id")
  private AccountHolder primaryOwner;

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
  private Money penaltyFee = new Money(new BigDecimal("40.00"));


  // ======================================== Constructors ========================================
  // ==================== Constructors with default penaltyFee ====================
  public Account(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.secondaryOwner = secondaryOwner;
  }

  public Account(Money balance, AccountHolder primaryOwner) {
    this.balance = balance;
    this.primaryOwner = primaryOwner;
    this.secondaryOwner = null;
  }


}
