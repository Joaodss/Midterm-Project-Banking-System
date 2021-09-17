package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import com.ironhack.midterm.util.validation.SavingsMinBalanceConstrain;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import static com.ironhack.midterm.util.helper.MoneyHelper.newBD;
import static com.ironhack.midterm.util.helper.MoneyHelper.newMoney;

@Entity
@Table(name = "savings_account")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class SavingsAccount extends Account {

  @NotNull
  @NotBlank
  @Column(name = "secret_key")
  private String secretKey;

  @Valid
  @NotNull
  @SavingsMinBalanceConstrain
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency", nullable = false))
  })
  private Money minimumBalance;

  @NotNull
  @Digits(integer = 1, fraction = 4)
  @DecimalMax(value = "0.5000")
  private BigDecimal interestRate;

  @NotNull
  @PastOrPresent
  @Column(name = "creation_date")
  private LocalDateTime creationDate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;


  // ======================================== Constructors ========================================
  // ==================== Constructors with default minimumBalance/interestRate/creationDate/status ====================
  public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey) {
    super(balance, primaryOwner, secondaryOwner);
    this.secretKey = secretKey;
    this.minimumBalance = newMoney("1000");
    this.interestRate = newBD("0.0025", 4);
    this.creationDate = LocalDateTime.now(ZoneId.of("Europe/London"));
    this.status = Status.ACTIVE;
  }

  public SavingsAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
    super(balance, primaryOwner);
    this.secretKey = secretKey;
    this.minimumBalance = newMoney("1000");
    this.interestRate = newBD("0.0025", 4);
    this.creationDate = LocalDateTime.now(ZoneId.of("Europe/London"));
    this.status = Status.ACTIVE;
  }


  // ======================================== Getters & Setters ========================================
  public void setMinimumBalance(Money minimumBalance) {
    if (minimumBalance.getAmount().compareTo(newBD("100")) < 0)
      throw new IllegalArgumentException("Invalid minimum balance amount. Must be equal or greater than 100€.");
    this.minimumBalance = minimumBalance;
  }

  public void setInterestRate(BigDecimal interestRate) {
    if (interestRate.compareTo(newBD("0.5")) > 0)
      throw new IllegalArgumentException("Invalid interest rate amount. Must be equal or lesser than 0.5.");
    this.interestRate = interestRate.setScale(4, RoundingMode.HALF_EVEN);
  }


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SavingsAccount that = (SavingsAccount) o;
    return getSecretKey().equals(that.getSecretKey()) && getMinimumBalance().equals(that.getMinimumBalance()) && getInterestRate().equals(that.getInterestRate()) && getCreationDate().equals(that.getCreationDate()) && getStatus() == that.getStatus();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSecretKey(), getMinimumBalance(), getInterestRate(), getCreationDate(), getStatus());
  }

}
