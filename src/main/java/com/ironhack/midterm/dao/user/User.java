package com.ironhack.midterm.dao.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static com.ironhack.midterm.util.EncryptedKeysUtil.encryptedKey;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "username", unique = true)
  private String username;

  @NotNull
  @Column(name = "password")
  private String password;

  @JsonIgnoreProperties(value = {"id", "users"}, allowSetters = true)
  @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
  )
  private Set<Role> roles = new HashSet<>();

  @NotNull
  @Column(name = "name")
  private String name;


  // ======================================== CONSTRUCTORS ========================================
  public User(String username, String password, String name) {
    this.username = username;
    this.password = encryptedKey(password);
    this.name = name;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
