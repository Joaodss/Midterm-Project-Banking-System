package com.ironhack.midterm.dto;

import com.ironhack.midterm.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewAccountDTO {

  @NotNull
  @NotBlank
  private String description;

  @Enumerated(EnumType.STRING)
  private AccountType accountType;

}
