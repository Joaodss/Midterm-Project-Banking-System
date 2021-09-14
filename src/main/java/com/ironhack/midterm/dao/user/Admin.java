package com.ironhack.midterm.dao.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Admin extends User {


  // ======================================== Constructors ========================================
  public Admin(String username, String password, String name) {
    super(username, password, name);
  }


}
