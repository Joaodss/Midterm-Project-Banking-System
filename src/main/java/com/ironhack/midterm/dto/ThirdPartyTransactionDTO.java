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
public class ThirdPartyTransactionDTO {

  @NotNull
  @DecimalMin(value = "0", message = "Initial balance must be positive")
  private BigDecimal transferValue;

  @NotBlank
  @Length(min = 3, max = 3, message = "The currency code must be a valid code of 3 letters.")
  private String currency;

  @NotNull
  @Positive(message = "Id of target account must be positive.")
  private long targetAccountId;

  @NotBlank(message = "Secret key cannot be empty or blank.")
  private String secretKey;

  @NotBlank(message = "Transaction purpose cannot be empty or blank.")
  private String transactionPurpose;

}
