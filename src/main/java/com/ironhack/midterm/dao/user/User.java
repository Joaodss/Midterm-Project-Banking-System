package com.ironhack.midterm.dao.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @UniqueElements
  @Length(min = 3)
  private String username;

  @NotBlank
  @Length(min = 5)
  private String password;

  @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
  @JoinTable(name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
  )
  private Set<Role> roles;

  @NotNull
  @NotBlank
  @Column(name = "name")
  private String name;


  // ======================================== Constructors ========================================
  public User(String username, String password, String name) {
    this.username = username;
    this.password = password;
    this.name = name;
  }


}
