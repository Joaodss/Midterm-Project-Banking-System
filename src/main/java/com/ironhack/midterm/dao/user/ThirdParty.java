package com.ironhack.midterm.dao.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.ironhack.midterm.util.EncryptedKeysUtil.encryptedKey;
import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;

@Entity
@Table(name = "third_party")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThirdParty extends User {

  @NotNull
  @NotBlank
  @Column(name = "hashed_key")
  private String hashedKey;


  // ======================================== CONSTRUCTORS ========================================
  public ThirdParty(String username, String password, String name) {
    super(username, password, name);
    try {
      this.hashedKey = encryptedKey(username + generateSecretKey());
    } catch (Exception e) {
      this.hashedKey = encryptedKey(username + password);
    }
  }

  // ======================================== METHODS ========================================
  public void updateHashedKey() {
    this.hashedKey = encryptedKey(hashCode() + getUsername() + getPassword() + getId());
  }


  // ======================================== OVERRIDE METHODS ========================================


}
