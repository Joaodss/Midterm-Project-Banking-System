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

import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;
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
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AccountStatus accountStatus;


  // ======================================== CONSTRUCTORS ========================================
  public CheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    super(balance, primaryOwner, secondaryOwner);
    try {
      this.secretKey = generateSecretKey();
    } catch (NoSuchAlgorithmException e) {
      this.secretKey = "0000000000";
    }
    this.minimumBalance = newMoney("250");
    this.monthlyMaintenanceFee = newMoney("12");
    this.accountStatus = AccountStatus.ACTIVE;
  }

  public CheckingAccount(Money balance, AccountHolder primaryOwner) {
    super(balance, primaryOwner);
    try {
      this.secretKey = generateSecretKey();
    } catch (NoSuchAlgorithmException e) {
      this.secretKey = "0000000000";
    }
    this.minimumBalance = newMoney("250");
    this.monthlyMaintenanceFee = newMoney("12");
    this.accountStatus = AccountStatus.ACTIVE;
  }


  // ======================================== METHODS ========================================
  public void updateSecretKey() {
    try {
      this.secretKey = generateSecretKey();
    } catch (NoSuchAlgorithmException ignored) {
    }
  }


  // ======================================== OVERRIDE METHODS ========================================


}
