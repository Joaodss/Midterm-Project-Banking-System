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

  @NotBlank(message = "The username can not be blank or empty")
  private String username;

  @NotBlank(message = "The password can not be blank or empty")
  private String password;

  @NotBlank(message = "The name can not be blank or empty")
  private String name;

}
