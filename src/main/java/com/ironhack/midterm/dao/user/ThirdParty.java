package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "third_party")
@PrimaryKeyJoinColumn(name = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ThirdParty extends User {

  // TODO JA - Study how to setup hashed key.
  @NotNull
  @NotBlank
  @Column(name = "hashed_key")
  private String hashedKey;


  // ======================================== Constructors ========================================
  public ThirdParty(String username, String password, String name, String hashedKey) {
    super(username, password, name);
    this.hashedKey = hashedKey;
  }


}
