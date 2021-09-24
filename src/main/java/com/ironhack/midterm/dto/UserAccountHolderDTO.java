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
public class UserAccountHolderDTO {

  @NotBlank(message = "Username cannot be empty or blank.")
  private String username;

  @NotBlank(message = "Password cannot be empty or blank.")
  private String password;

  @NotBlank(message = "Name cannot be empty or blank.")
  private String name;

  @NotNull(message = "Date of birth cannot be empty or blank.")
  private LocalDate dateOfBirth;

  @NotBlank(message = "Primary street address cannot be empty or blank.")
  private String paStreetAddress;

  @NotBlank(message = "Primary address postal code cannot be empty or blank.")
  private String paPostalCode;

  @NotBlank(message = "Primary address city cannot be empty or blank.")
  private String paCity;

  @NotBlank(message = "Primary address country cannot be empty or blank.")
  private String paCountry;

  private String maStreetAddress;

  private String maPostalCode;

  private String maCity;

  private String maCountry;

}
