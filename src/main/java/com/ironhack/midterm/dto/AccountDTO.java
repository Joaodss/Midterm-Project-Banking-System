package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {

  @NotNull(message = "Initial balance must be positive and not null.")
  @PositiveOrZero(message = "Initial balance must be positive and not null.")
  private BigDecimal initialBalance;

  @NotBlank(message = "The currency code must be a valid code of 3 letters, and not blank.")
  @Length(min = 3, max = 3, message = "The currency code must be a valid code of 3 letters.")
  private String currency;

  @NotNull(message = "Primary owner id must be positive and not null.")
  @Positive(message = "Primary owner id must be positive and not null.")
  private Long primaryOwnerId;

  @NotBlank(message = "Primary owner cannot be blank or empty.")
  private String primaryOwnerUsername;

  @Positive(message = "Secondary owner id must be positive and exist.")
  private Long secondaryOwnerId;

  private String secondaryOwnerUsername;

}
