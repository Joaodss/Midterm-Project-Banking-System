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
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;
import static com.ironhack.midterm.util.MoneyUtil.newBD;
import static com.ironhack.midterm.util.MoneyUtil.newMoney;

@Entity
@Table(name = "savings_account")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavingsAccount extends Account {

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

  @NotNull
  @Column(name = "interest_rate")
  private BigDecimal interestRate;

  @NotNull
  @Column(name = "last_interest_update")
  private LocalDateTime lastInterestUpdate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AccountStatus accountStatus;


  // ======================================== CONSTRUCTORS ========================================
  public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner) {
    super(balance, primaryOwner, secondaryOwner);
    try {
      this.secretKey = generateSecretKey();
    } catch (NoSuchAlgorithmException e) {
      this.secretKey = "0000000000";
    }
    this.minimumBalance = newMoney("1000");
    this.interestRate = newBD("0.0025");
    this.lastInterestUpdate = getCreationDate();
    this.accountStatus = AccountStatus.ACTIVE;
  }

  public SavingsAccount(Money balance, AccountHolder primaryOwner) {
    super(balance, primaryOwner);
    try {
      this.secretKey = generateSecretKey();
    } catch (NoSuchAlgorithmException e) {
      this.secretKey = "0000000000";
    }
    this.minimumBalance = newMoney("1000");
    this.interestRate = newBD("0.0025");
    this.lastInterestUpdate = getCreationDate();
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
