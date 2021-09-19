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

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  private String name;

  @NotNull
  private LocalDate dateOfBirth;

  @NotNull
  @NotBlank
  private String paStreetAddress;

  @NotBlank
  private String paPostalCode;

  @NotBlank
  private String paCity;

  @NotBlank
  private String paCountry;

  private String maStreetAddress;

  private String maPostalCode;

  private String maCity;

  private String maCountry;


  // ======================================== CONSTRUCTORS ========================================
  public AccountHolderDTO(String username, String password, String name, LocalDate dateOfBirth, String paStreetAddress, String paPostalCode, String paCity, String paCountry) {
    this.username = username;
    this.password = password;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
    this.paStreetAddress = paStreetAddress;
    this.paPostalCode = paPostalCode;
    this.paCity = paCity;
    this.paCountry = paCountry;
  }


}
