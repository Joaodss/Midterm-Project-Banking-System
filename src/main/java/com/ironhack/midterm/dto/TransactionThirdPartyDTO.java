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
public class TransactionThirdPartyDTO {

  @NotNull(message = "Transfer value must be positive and not null.")
  @PositiveOrZero(message = "Transfer value must be positive.")
  private BigDecimal transferValue;

  @NotBlank(message = "The currency code must be a valid code of 3 letters, and not blank.")
  @Length(min = 3, max = 3, message = "The currency code must be a valid code of 3 letters.")
  private String currency;

  @NotNull(message = "Target account id must be positive and exist. Cannot be null.")
  @Positive(message = "Target account id must be positive and exist.")
  private long targetAccountId;

  @NotBlank(message = "Secret key cannot be empty or blank.")
  private String secretKey;

  @NotBlank(message = "Transaction purpose cannot be empty or blank.")
  private String transactionPurpose;

}
