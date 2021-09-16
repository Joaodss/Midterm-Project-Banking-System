package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "third_party")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ThirdParty extends User {

  // TODO JA - Study how to setup hashed key.
  @NotNull
  @NotBlank
  @Column(name = "hashed_key")
  private String hashedKey;


  // ======================================== Constructors ========================================
  public ThirdParty(String username, String password, String name) {
    super(username, password, name);
    this.hashedKey = "hashedKey";
  }

  // ======================================== Getters & Setters ========================================


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ThirdParty that = (ThirdParty) o;
    return getHashedKey().equals(that.getHashedKey());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getHashedKey());
  }

}
