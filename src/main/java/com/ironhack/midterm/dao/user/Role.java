package com.ironhack.midterm.dao.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @NotBlank
  private String name;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();


  // ======================================== CONSTRUCTORS ========================================
  public Role(String name) {
    this.name = name;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================

}
