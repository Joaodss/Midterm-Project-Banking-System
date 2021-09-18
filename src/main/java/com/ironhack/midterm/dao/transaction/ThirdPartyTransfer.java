package com.ironhack.midterm.dao.transaction;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.enums.Purpose;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "third_party_transfer")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThirdPartyTransfer extends Transaction {

  @NotNull
  @JsonIncludeProperties(value = {"id", "primaryOwner", "secondaryOwner"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_account_id")
  private Account targetAccount;

  @NotNull
  @Column(name = "secret_key")
  private String secretKey;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "purpose")
  private Purpose purpose;


  // ======================================== CONSTRUCTORS ========================================
  public ThirdPartyTransfer(Money baseAmount, Money convertedAmount, Status status, Account targetAccount, String secretKey, Purpose purpose) {
    super(baseAmount, convertedAmount, status);
    this.targetAccount = targetAccount;
    this.secretKey = secretKey;
    this.purpose = purpose;
  }

  public ThirdPartyTransfer(Money baseAmount, Status status, Account targetAccount, String secretKey, Purpose purpose) {
    super(baseAmount, status);
    this.targetAccount = targetAccount;
    this.secretKey = secretKey;
    this.purpose = purpose;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
