package com.ironhack.midterm.user.dao;

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
public class Admin extends UserType {

    // ======================================== Constructors ========================================
    public Admin(String name) {
        super(name);
    }


}
