package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountEditDTO {

  private String primaryOwnerUsername;

  private String secondaryOwnerUsername;

  private String accountStatus;   // for all except credit card

  @Length(min = 3, max = 3, message = "The currency code must be a valid code of 3 letters.")
  private String currency;

  @PositiveOrZero(message = "Balance must be positive or zero.")
  private BigDecimal accountBalance;

  // Penalty Fee
  @PositiveOrZero(message = "Penalty fee must be positive or zero.")
  private BigDecimal penaltyFee;

  private LocalDate lastPenaltyFee;

  // Minimum Balance
  @PositiveOrZero(message = "Minimum balance must be positive or zero.")
  private BigDecimal MinimumBalance;

  // Credit Limit
  @PositiveOrZero(message = "Credit Limit must be positive or zero.")
  private BigDecimal creditLimit;

  // Maintenance Fee
  @PositiveOrZero(message = "Monthly maintenance fee must be positive or zero.")
  private BigDecimal monthlyMaintenanceFee;

  private LocalDate lastMaintenanceFee;

  // Interest Rate
  @PositiveOrZero(message = "Savings account interest rate must be positive or zero.")
  @DecimalMax(value = "0.5", message = "Savings account interest rate must be lower than 0.5")
  private BigDecimal savingsAccountInterestRate;

  @DecimalMin(value = "0.1", message = "Credit Card interest rate must be greater than 0.1")
  private BigDecimal creditCardInterestRate;

  private LocalDate lastInterestUpdate;

}
