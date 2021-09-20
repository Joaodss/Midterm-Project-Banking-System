package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {

  @DecimalMin(value = "0", message = "Initial balance must be positive")
  private BigDecimal initialBalance;

  @NotBlank
  @Length(min = 3, max = 3, message = "The currency code must be a valid code of 3 letters.")
  private String currency;

  @NotNull(message = "Primary owner id must be positive and not null.")
  @Positive(message = "Primary owner id must be positive and not null.")
  private Long primaryOwnerId;

  @NotBlank(message = "Primary owner can not be blank or empty")
  private String primaryOwnerUsername;

  @Positive(message = "Secondary owner id must be positive.")
  private Long secondaryOwnerId;

  private String secondaryOwnerUsername;

}
