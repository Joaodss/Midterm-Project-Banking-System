package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.model.Money;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Objects;

import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;
import static com.ironhack.midterm.util.MoneyUtil.convertCurrency;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "checking_account")
@PrimaryKeyJoinColumn(name = "id")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CheckingAccount extends Account {

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
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "monthly_maintenance_fee_currency", nullable = false))
  })
  private Money monthlyMaintenanceFee;

  @NotNull
  @Column(name = "last_maintenance_fee")
  private LocalDate lastMaintenanceFee;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AccountStatus accountStatus;


  // ======================================== CONSTRUCTORS ========================================
  // Constructor with primary and secondary owners.
  // Set default values for minimumBalance(250 €), monthlyMaintenanceFee(12 €), lastMaintenanceFee(start of next month),
  // accountStatus(active), and generate secretKey(random key).
  public CheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner, secondaryOwner);
    super.setAccountType(AccountType.CHECKING_ACCOUNT);
    this.minimumBalance = newMoney("250.00");
    this.monthlyMaintenanceFee = newMoney("12.00");
    this.lastMaintenanceFee = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
    this.accountStatus = AccountStatus.ACTIVE;
    this.secretKey = generateSecretKey();
  }

  // Constructor only with primary owner.
  // Set default values for minimumBalance(250 €), monthlyMaintenanceFee(12 €), lastMaintenanceFee(start of next month),
  // accountStatus(active), and generate secretKey(random key).
  public CheckingAccount(Money balance, AccountHolder primaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner);
    super.setAccountType(AccountType.CHECKING_ACCOUNT);
    this.minimumBalance = newMoney("250.00");
    this.monthlyMaintenanceFee = newMoney("12.00");
    this.lastMaintenanceFee = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
    this.accountStatus = AccountStatus.ACTIVE;
    this.secretKey = generateSecretKey();
  }


  // ======================================== METHODS ========================================
  // Converts all money values to have the same currency as the account's balance.
  public void updateCurrencyValues() {
    setPenaltyFee(convertCurrency(getBalance(), getPenaltyFee()));
    setMinimumBalance(convertCurrency(getBalance(), getMinimumBalance()));
    setMonthlyMaintenanceFee(convertCurrency(getBalance(), getMonthlyMaintenanceFee()));
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CheckingAccount that = (CheckingAccount) o;
    return getSecretKey().equals(that.getSecretKey()) && getMinimumBalance().equals(that.getMinimumBalance()) && getMonthlyMaintenanceFee().equals(that.getMonthlyMaintenanceFee()) && getLastMaintenanceFee().equals(that.getLastMaintenanceFee()) && getAccountStatus() == that.getAccountStatus();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSecretKey(), getMinimumBalance(), getMonthlyMaintenanceFee(), getLastMaintenanceFee(), getAccountStatus());
  }

}
