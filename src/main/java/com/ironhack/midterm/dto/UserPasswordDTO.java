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
public class UserPasswordDTO {

  @NotBlank
  private String currentPassword;

  @NotBlank
  private String newPassword;

  @NotBlank
  private String repeatedNewPassword;


}
