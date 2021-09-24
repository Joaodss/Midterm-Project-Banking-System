package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.model.Money;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Objects;

import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;
import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

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
  @Column(name = "secret_key")
  private String secretKey;

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency", nullable = false))
  })
  private Money minimumBalance;

  @NotNull
  @Column(name = "interest_rate")
  private BigDecimal interestRate;

  @NotNull
  @Column(name = "last_interest_update")
  private LocalDate lastInterestUpdate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AccountStatus accountStatus;


  // ======================================== CONSTRUCTORS ========================================
  public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner, secondaryOwner);
    super.setAccountType(AccountType.SAVINGS_ACCOUNT);
    this.minimumBalance = newMoney("1000.00");
    this.interestRate = new BigDecimal("0.0025");
    this.lastInterestUpdate = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
    this.accountStatus = AccountStatus.ACTIVE;
    this.secretKey = generateSecretKey();
  }

  public SavingsAccount(Money balance, AccountHolder primaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner);
    super.setAccountType(AccountType.SAVINGS_ACCOUNT);
    this.minimumBalance = newMoney("1000.00");
    this.interestRate = new BigDecimal("0.0025");
    this.lastInterestUpdate = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
    this.accountStatus = AccountStatus.ACTIVE;
    this.secretKey = generateSecretKey();
  }


  // ======================================== METHODS ========================================
  public void updateCurrencyValues() {
    setPenaltyFee(convertCurrency(getBalance(), getPenaltyFee()));
    setMinimumBalance(convertCurrency(getBalance(), getMinimumBalance()));
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SavingsAccount that = (SavingsAccount) o;
    return getSecretKey().equals(that.getSecretKey()) && getMinimumBalance().equals(that.getMinimumBalance()) && getInterestRate().equals(that.getInterestRate()) && getLastInterestUpdate().equals(that.getLastInterestUpdate()) && getAccountStatus() == that.getAccountStatus();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSecretKey(), getMinimumBalance(), getInterestRate(), getLastInterestUpdate(), getAccountStatus());
  }

}
