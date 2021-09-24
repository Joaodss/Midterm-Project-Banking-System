package com.ironhack.midterm.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class Money {

  private static final Currency EUR = Currency.getInstance("EUR");
  private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

  @NotNull
  private final Currency currency;

  @NotNull
  @PositiveOrZero
  private BigDecimal amount;


  // ======================================== Constructors ========================================
  public Money() {
    this.currency = Currency.getInstance("EUR");
  }

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

  // ======================================== Override Methods ========================================
  @Override
  public String toString() {
    return getCurrency().getSymbol() + " " + getAmount();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Money money = (Money) o;
    return getCurrency().equals(money.getCurrency()) && getAmount().equals(money.getAmount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCurrency(), getAmount());
  }

}
