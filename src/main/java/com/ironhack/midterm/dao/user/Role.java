package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  private String name;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
  private Set<User> users = Collections.emptySet();


  // ======================================== Constructors ========================================
  public Role(String name) {
    this.name = name;
  }


  // ======================================== Getters & Setters ========================================


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return getId().equals(role.getId()) && getName().equals(role.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }

}
