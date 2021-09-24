package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

  private String username;

  private String password;

  private String name;

  private LocalDate dateOfBirth;

  private String paStreetAddress;

  private String paPostalCode;

  private String paCity;

  private String paCountry;

  private String maStreetAddress;

  private String maPostalCode;

  private String maCity;

  private String maCountry;

}
