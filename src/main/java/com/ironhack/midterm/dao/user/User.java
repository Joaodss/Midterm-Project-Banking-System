package com.ironhack.midterm.dao.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.ironhack.midterm.util.EncryptedKeysUtil.encryptedKey;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "username", unique = true)
  private String username;

  @ToString.Exclude
  @NotNull
  @Column(name = "password")
  private String password;

  @JsonIncludeProperties(value = {"name"})
  @ToString.Include(name = "name")
  @ManyToMany(fetch = FetchType.EAGER)
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


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return getId().equals(user.getId()) && getUsername().equals(user.getUsername()) && getName().equals(user.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername(), getName());
  }

}
