package com.ironhack.midterm.dao.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  private String name;

  @ManyToMany(mappedBy = "roles")
  private Set<User> users;


  // ======================================== Constructors ========================================
  public Role(String name) {
    this.name = name;
  }


}
