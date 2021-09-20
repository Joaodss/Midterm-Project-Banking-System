package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountHolderDTO {

  @NotBlank(message = "The username can not be blank or empty")
  private String username;

  @NotBlank(message = "The password can not be blank or empty")
  private String password;

  @NotBlank(message = "The name can not be blank or empty")
  private String name;

  @NotNull
  private LocalDate dateOfBirth;

  @NotNull
  @NotBlank(message = "The primary street address can not be blank or empty")
  private String paStreetAddress;

  @NotBlank(message = "The primary postal code can not be blank or empty")
  private String paPostalCode;

  @NotBlank(message = "The primary city can not be blank or empty")
  private String paCity;

  @NotBlank(message = "The primary country can not be blank or empty")
  private String paCountry;

  private String maStreetAddress;

  private String maPostalCode;

  private String maCity;

  private String maCountry;

}
