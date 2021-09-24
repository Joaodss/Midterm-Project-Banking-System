package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.ironhack.midterm.util.EncryptedKeysUtil.encryptedKey;
import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;

@Entity
@Table(name = "third_party")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ThirdParty extends User {

  @NotNull
  @Column(name = "hashed_key")
  private String hashedKey;


  // ======================================== CONSTRUCTORS ========================================
  public ThirdParty(String username, String password, String name) {
    super(username, password, name);
    // Generates encrypted key from username and randomly generated key. If fails, uses username and password.
    try {
      this.hashedKey = encryptedKey(username + generateSecretKey());
    } catch (Exception e) {
      this.hashedKey = encryptedKey(username + password);
    }
  }


  // ======================================== OVERRIDE METHODS ========================================
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
