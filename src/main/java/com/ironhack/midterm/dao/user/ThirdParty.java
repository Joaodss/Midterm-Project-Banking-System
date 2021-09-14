package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "third_party")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ThirdParty extends UserType {

  // TODO JA - Study how to setup hashed key.
  @NotNull
  @NotBlank
  @Column(name = "hashed_key")
  private String hashedKey;


  // ======================================== Constructors ========================================
  public ThirdParty(String name, String hashedKey) {
    super(name);
    this.hashedKey = hashedKey;
  }


}
