package com.ironhack.midterm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class Money {

  private static final Currency EUR = Currency.getInstance("EUR");
  private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

  @NotNull
  private final Currency currency;

  @NotNull
  @PositiveOrZero
  private BigDecimal amount;


  // ======================================== Constructors ========================================
  // Class constructor specifying amount, currency, and rounding
  public Money(BigDecimal amount, Currency currency, RoundingMode rounding) {
    this.currency = currency;
    setAmount(amount.setScale(currency.getDefaultFractionDigits(), rounding));
  }

  // Class constructor specifying amount, and currency. Uses default RoundingMode HALF_EVEN.
  public Money(BigDecimal amount, Currency currency) {
    this(amount, currency, DEFAULT_ROUNDING);
  }

  // Class constructor specifying amount. Uses default RoundingMode HALF_EVEN and default currency USD.
  public Money(BigDecimal amount) {
    this(amount, EUR, DEFAULT_ROUNDING);
  }


  // ======================================== Methods ========================================
  public BigDecimal increaseAmount(Money money) {
    setAmount(this.amount.add(money.amount));
    return this.amount;
  }

  public BigDecimal increaseAmount(BigDecimal addAmount) {
    setAmount(this.amount.add(addAmount));
    return this.amount;
  }

  public BigDecimal decreaseAmount(Money money) {
    setAmount(this.amount.subtract(money.getAmount()));
    return this.amount;
  }

  public BigDecimal decreaseAmount(BigDecimal addAmount) {
    setAmount(this.amount.subtract(addAmount));
    return this.amount;
  }

  public String toString() {
    return getCurrency().getSymbol() + " " + getAmount();
  }


}
