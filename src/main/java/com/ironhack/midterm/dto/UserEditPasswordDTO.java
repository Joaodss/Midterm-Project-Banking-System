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
public class UserEditPasswordDTO {

  @NotBlank(message = "Current password cannot be empty.")
  private String currentPassword;

  @NotBlank(message = "New password cannot be empty or blank.")
  private String newPassword;

  @NotBlank(message = "New password cannot be empty or blank.")
  private String repeatedNewPassword;

}
