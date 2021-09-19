package com.ironhack.midterm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThirdPartyDTO {

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  private String name;

}
