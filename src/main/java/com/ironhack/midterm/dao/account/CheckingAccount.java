package com.ironhack.midterm.dao.account;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountStatus;
import com.ironhack.midterm.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

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
public class CheckingAccount extends Account {

  @NotNull
  @Column(name = "secret_key")
  private String secretKey;

  @Valid
  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency", nullable = false))
  })
  private Money minimumBalance;

  @Valid
  @NotNull
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
  public CheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner, secondaryOwner);
    this.minimumBalance = newMoney("250");
    this.monthlyMaintenanceFee = newMoney("12");
    this.lastMaintenanceFee = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
    this.accountStatus = AccountStatus.ACTIVE;
    this.secretKey = generateSecretKey();
  }

  public CheckingAccount(Money balance, AccountHolder primaryOwner) throws NoSuchAlgorithmException {
    super(balance, primaryOwner);
    this.minimumBalance = newMoney("250");
    this.monthlyMaintenanceFee = newMoney("12");
    this.lastMaintenanceFee = getCreationDate().toLocalDate().withDayOfMonth(1).plusMonths(1);
    this.accountStatus = AccountStatus.ACTIVE;
    this.secretKey = generateSecretKey();
  }


  // ======================================== METHODS ========================================
  public void updateCurrencyValues() {
    setPenaltyFee(convertCurrency(getBalance(), getPenaltyFee()));
    setMinimumBalance(convertCurrency(getBalance(), getMinimumBalance()));
    setMonthlyMaintenanceFee(convertCurrency(getBalance(), getMonthlyMaintenanceFee()));
  }


  // ======================================== OVERRIDE METHODS ========================================


}
