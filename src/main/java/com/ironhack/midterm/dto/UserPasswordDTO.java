package com.ironhack.midterm.dto;

import javax.validation.constraints.NotBlank;

public class UserPasswordDTO {

  @NotBlank
  private String CurrentPassword;

  @NotBlank
  private String newPassword;

  @NotBlank
  private String repeatedNewPassword;


}
