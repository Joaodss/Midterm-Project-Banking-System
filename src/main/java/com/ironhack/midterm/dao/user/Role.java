package com.ironhack.midterm.dao.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
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

  @NotNull
  @Column(name = "name", unique = true)
  private String name;

  @ToString.Exclude
  @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();


  // ======================================== CONSTRUCTORS ========================================
  public Role(String name) {
    this.name = name;
  }


  // ======================================== OVERRIDE METHODS ========================================
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
